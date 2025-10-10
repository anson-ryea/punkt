package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.options.SyncOptions
import com.an5on.config.ActiveConfiguration
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtils.existingLocalPathsToActivePaths
import com.an5on.operation.OperationUtils.expand
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionMakeDirectories
import com.an5on.command.Echos
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import org.apache.commons.io.filefilter.RegexFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

object SyncOperation {
    fun sync(activePaths: Set<Path>?, options: SyncOptions, echo: Echos): Either<PunktError, Unit> = either {
        if (activePaths == null || activePaths.isEmpty()) {
            syncExistingLocal(options, echo).bind()
        } else {
            syncPaths(activePaths, options, echo).bind()
        }
    }

    private fun syncPaths(activePaths: Set<Path>, options: SyncOptions, echo: Echos): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.any { it.startsWith(localDirAbsPath) }) {
            echo.echoWarning(
                "Directories and files in the local Punkt directory (${ActiveConfiguration.localDirAbsPathname}) will not be synced."
            )
        }

        val includeExcludeFilter = RegexFileFilter(options.include.pattern)
            .and(RegexFileFilter(options.exclude.pattern).negate())
            .and(ActiveEqualsLocalFileFilter.negate())

        val expandedActivePaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
            acc.addAll(
                activePath.expand(options.recursive, includeExcludeFilter).bind()
            )
            acc
        }

        LocalState.pendingTransactions.addAll(
            expandedActivePaths.map { activePath ->
                println("Syncing: $activePath")
                if (activePath.isDirectory()) {
                    LocalTransactionMakeDirectories(activePath)
                } else {
                    LocalTransactionCopyToLocal(activePath)
                }
            }
        )

        LocalState.commit().bind()
    }

    private fun syncExistingLocal(options: SyncOptions, echo: Echos): Either<PunktError, Unit> = either {
        syncPaths(existingLocalPathsToActivePaths.bind(), options, echo).bind()
    }
}