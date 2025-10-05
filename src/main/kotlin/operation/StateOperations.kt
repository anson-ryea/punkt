package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalState.getActiveFile
import com.an5on.states.local.LocalState.getLocalFile
import com.an5on.states.tracked.TrackedEntriesStore
import com.an5on.states.tracked.TrackedEntryDir
import com.an5on.states.tracked.TrackedEntryFile
import com.an5on.utils.CliktEcho
import com.an5on.utils.FileUtils.getBlake3HashHexString
import com.github.ajalt.clikt.core.CliktError
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes
import java.nio.file.attribute.DosFileAttributes
import kotlin.io.path.PathWalkOption
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo
import kotlin.io.path.walk


object StateOperations {
    fun sync(activeFile: File, options: SyncOptions, echo: CliktEcho): Either<CliktError, Unit> = either {

        var filteredActiveFiles = if (options.recursive) {
            activeFile.walk().filter { !it.path.matches(options.exclude) }.filter { it.path.matches(options.include) }
        } else {
            when (activeFile.isDirectory) {
                true -> activeFile.listFiles()!!.toList()
                false -> listOf(activeFile)
            }.filter { !it.path.matches(options.exclude) }.filter { it.path.matches(options.include) }.asSequence()
        }

        if (filteredActiveFiles.any { it.path == ActiveConfiguration.localDirAbsPathname }) {
            echo(
                "Directories and files in the local Punkt directory (${ActiveConfiguration.localDirAbsPathname}) will not be synced.",
                true,
                false
            )
            filteredActiveFiles =
                filteredActiveFiles.filter { !it.path.startsWith(ActiveConfiguration.localDirAbsPathname) }
        }

        for (activeFile in filteredActiveFiles) {
            val relativePath = activeFile.relativeTo(File(ActiveConfiguration.homeDirAbsPath.pathString)).toPath()
            val localFile = getLocalFile(relativePath)

            if (localFile.exists()) {
                when (activeFile.isDirectory) {
                    true -> continue
                    false -> {
                        val trackedEntryFile = TrackedEntriesStore[activeFile.toPath()] as TrackedEntryFile?
                        if (trackedEntryFile != null && (
                                    activeFile.lastModified() == localFile.lastModified() &&
                                            getBlake3HashHexString(activeFile) == getBlake3HashHexString(localFile))
                        ) {
                            continue
                        }
                    }
                }
            }

            if (activeFile.isDirectory) {
                TrackedEntriesStore[activeFile.toPath()] = TrackedEntryDir()

                if (!localFile.exists()) {
                    localFile.mkdirs()
                    echo("Created directory: ${localFile.path}", true, false)
                }
            } else {
                if (!localFile.parentFile.exists()) {
                    localFile.parentFile.mkdirs()
                    echo("Created directory: ${localFile.parentFile.path}", true, false)
                }

                if (!localFile.exists() || localFile.readText() != activeFile.readText()) {
                    val activeFileBasicAttributes =
                        Files.readAttributes(activeFile.toPath(), BasicFileAttributes::class.java)
                    Files.readAttributes(activeFile.toPath(), DosFileAttributes::class.java)
                    TrackedEntriesStore[activeFile.toPath()] = TrackedEntryFile(
                        activeFileBasicAttributes.lastModifiedTime().toMillis(),
                        getBlake3HashHexString(activeFile)
                    )

                    LocalState.copyFileFromActiveToLocal(activeFile.toPath())
                    echo("Synced file: ${localFile.path}", true, false)
                }
            }
        }
    }

    fun syncExistingLocal(options: SyncOptions, echo: CliktEcho): Either<CliktError, Unit> = either {
        val activeFilesInLocal = ActiveConfiguration.localDirAbsPath
            .walk(PathWalkOption.BREADTH_FIRST)
            .filter { it != ActiveConfiguration.localDirAbsPath }
            .map { getActiveFile(it.relativeTo(ActiveConfiguration.localDirAbsPath)) }
            .filter { it.exists() }

        for (activeFile in activeFilesInLocal) {
            sync(activeFile, options, echo).bind()
        }
    }
}