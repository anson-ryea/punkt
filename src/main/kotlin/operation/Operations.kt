package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.active.ActiveState.contentEqualsActive
import com.an5on.states.active.ActiveState.existsInActive
import com.an5on.states.active.ActiveState.toActivePath
import com.an5on.states.active.ActiveTransaction
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalState.contentEqualsLocal
import com.an5on.states.local.LocalState.existsInLocal
import com.an5on.states.local.LocalState.toLocalPath
import com.an5on.states.local.LocalTransaction
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionMakeDirectories
import com.an5on.states.tracked.TrackedEntriesStore
import com.an5on.states.tracked.TrackedEntryDir
import com.an5on.states.tracked.TrackedEntryFile
import com.an5on.utils.Echos
import com.an5on.utils.FileUtils.getBlake3HashHexString
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.*

object Operations {
    fun sync(activePaths: Set<Path>, options: SyncOptions, echo: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        var accumulatedActivePaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
            if (!activePath.isDirectory()) {
                if (!activePath.pathString.matches(options.exclude)
                        && activePath.pathString.matches(options.include)) {
                        acc.add(activePath)
                }
            } else if (options.recursive) {
                acc.addAll(
                    activePath
                        .walk()
                        .filter {
                            !it.pathString.matches(options.exclude)
                                    && it.pathString.matches(options.include)
                        }
                )
            } else {
                acc.addAll(
                    activePath
                        .listDirectoryEntries()
                        .filter {
                            !it.pathString.matches(options.exclude)
                                    && it.pathString.matches(options.include)
                        }
                )
            }
            acc
        }

        if (accumulatedActivePaths.any { it.startsWith(ActiveConfiguration.localDirAbsPath) }) {
            echo.echoWarning(
                "Directories and files in the local Punkt directory (${ActiveConfiguration.localDirAbsPathname}) will not be synced."
            )
            accumulatedActivePaths =
                accumulatedActivePaths
                    .filter { !it.startsWith(ActiveConfiguration.localDirAbsPathname) }
                    .toMutableSet()
        }

        val localTransactions = accumulatedActivePaths.fold(mutableSetOf<LocalTransaction>()) { acc, activePath ->
            if (activePath.existsInLocal().bind()) {
                if (!activePath.isDirectory() && !activePath.contentEqualsLocal().bind()) {
                    acc.add(LocalTransactionCopyToLocal(activePath))
                }
            } else if (activePath.isDirectory()) {
                TrackedEntriesStore[activePath] = TrackedEntryDir()
                acc.add(LocalTransactionMakeDirectories(activePath))
            } else {
                val activeFileBasicAttributes =
                    Files.readAttributes(activePath, BasicFileAttributes::class.java)

                TrackedEntriesStore[activePath] = TrackedEntryFile(
                    activeFileBasicAttributes.lastModifiedTime().toMillis(),
                    getBlake3HashHexString(activePath.toFile())
                )
                acc.add(LocalTransactionCopyToLocal(activePath))
            }
            acc
        }

        localTransactions.forEach {
            it.run().bind()
        }
    }

    fun syncExistingLocal(options: SyncOptions, echo: Echos): Either<PunktError, Unit> = either {
        val activePathsInLocal = ActiveConfiguration.localDirAbsPath
            .walk(PathWalkOption.BREADTH_FIRST)
            .filter { it != ActiveConfiguration.localDirAbsPath }
            .map { it.toActivePath().bind() }
            .filter { it.exists() }
            .toSet()

        sync(activePathsInLocal, options, echo).bind()
    }

    fun activate(activePaths: Set<Path>, options: ActivateOptions, echo: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val accumulatedLocalPaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
            val localPath = activePath.toLocalPath().bind()

            if (localPath.exists()) {
                if (!localPath.isDirectory()) {
                    if (!activePath.pathString.matches(options.exclude)
                        && activePath.pathString.matches(options.include)
                    ) {
                        acc.add(localPath)
                    }
                } else if (options.recursive) {
                    acc.addAll(
                        localPath
                            .walk()
                            .filter {
                                val walkingActivePath = it.toActivePath().bind()
                                !walkingActivePath.pathString.matches(options.exclude)
                                        && walkingActivePath.pathString.matches(options.include)
                            }
                    )
                } else {
                    acc.addAll(
                        localPath
                            .listDirectoryEntries()
                            .filter {
                                val walkingActivePath = it.toActivePath().bind()
                                !walkingActivePath.pathString.matches(options.exclude)
                                        && walkingActivePath.pathString.matches(options.include)
                            }
                    )
                }
            }
            acc
        }

        val activeTransactions = accumulatedLocalPaths.fold(mutableSetOf<ActiveTransaction>()) { acc, localPath ->
            if (localPath.existsInActive().bind()) {
                if (!localPath.isDirectory() && !localPath.contentEqualsActive().bind()) {
                    acc.add(ActiveTransactionCopyToActive(localPath))
                }
            } else if (localPath.isDirectory()) {
                acc.add(ActiveTransactionMakeDirectories(localPath))
            } else {
                acc.add(ActiveTransactionCopyToActive(localPath))
            }

            acc
        }

        activeTransactions.forEach {
            it.run().bind()
        }
    }

    fun activateExistingLocal(options: ActivateOptions, echo: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val localPathsInLocal = ActiveConfiguration.localDirAbsPath
            .walk(PathWalkOption.BREADTH_FIRST)
            .filter { it != ActiveConfiguration.localDirAbsPath }


        val activeTransactions = localPathsInLocal.fold(mutableSetOf<ActiveTransaction>()) { acc, localPath ->
            if (localPath.existsInActive().bind()) {
                if (!localPath.isDirectory() && !localPath.contentEqualsActive().bind()) {
                    acc.add(ActiveTransactionCopyToActive(localPath))
                }
            } else if (localPath.isDirectory()) {
                acc.add(ActiveTransactionMakeDirectories(localPath))
            } else {
                acc.add(ActiveTransactionCopyToActive(localPath))
            }

            acc
        }

        activeTransactions.forEach {
            it.run().bind()
        }
    }
}