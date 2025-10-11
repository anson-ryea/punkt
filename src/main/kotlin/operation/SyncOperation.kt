package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.SyncOptions
import com.an5on.config.ActiveConfiguration
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.operation.OperationUtils.existingLocalPathsToActivePaths
import com.an5on.operation.OperationUtils.expand
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionMakeDirectories
import org.apache.commons.io.filefilter.RegexFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

object SyncOperation {
    fun Raise<PunktError>.sync(activePaths: Set<Path>?, options: SyncOptions, echo: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths == null || activePaths.isEmpty()) {
            syncExistingLocal(options, echo)
        } else {
            syncPaths(activePaths, options, echo)
        }
    }

    private fun Raise<PunktError>.syncPaths(activePaths: Set<Path>, options: SyncOptions, echo: Echos) {

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
                activePath.expand(options.recursive, includeExcludeFilter)
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

        LocalState.commit()
    }

    private fun Raise<PunktError>.syncExistingLocal(options: SyncOptions, echo: Echos) {
        syncPaths(existingLocalPathsToActivePaths, options, echo)
    }
}