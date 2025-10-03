package com.an5on.operation

import com.an5on.config.Configuration
import com.an5on.states.local.LocalState.getLocalPath
import com.an5on.utils.CliktEcho

import java.io.File
import java.nio.file.Files
import kotlin.io.path.pathString

object ManageState {
    fun sync(target: File, options: SyncOptions, echo: CliktEcho) {

        var filteredTargets = target.walk().filter { it.canRead() && !it.name.matches(options.exclude) }

        if (filteredTargets.any { it.path == Configuration.active.localDirAbsPathname }) {
            echo("Directories and files in the local Punkt directory (${Configuration.active.localDirAbsPathname}) will not be synced.", true, false)
            filteredTargets = filteredTargets.filter { !it.path.startsWith(Configuration.active.localDirAbsPathname) }
        }

        for (target in filteredTargets) {
            val relativePathname = target.relativeTo(File(Configuration.active.homeDirAbsPathname)).path
            val localFile = File(getLocalPath(relativePathname).pathString)
            val activeFile = File(target.path)
//            if (TrackedEntriesStore.get(localFile.toPath()) != null) {
//
//            }

            if (target.isDirectory) {
                if (options.recursive && !localFile.exists()) {
                    localFile.mkdirs()
                    echo("Created directory: ${localFile.path}", true, false)
                }
            } else {
                if (!localFile.parentFile.exists()) {
                    localFile.parentFile.mkdirs()
                    echo("Created directory: ${localFile.parentFile.path}", true, false)
                }
                if (!localFile.exists() || target.readText() != localFile.readText()) {
                    Files.copy(target.toPath(), localFile.toPath())
                    echo("Synced file: ${localFile.path}", true, false)
                }
            }
        }
    }
}