package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.ActivateOptions
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveState
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.states.local.LocalState
import org.apache.commons.io.filefilter.TrueFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

object ActivateOperation {
    fun Raise<PunktError>.activate(activePaths: Set<Path>?, options: ActivateOptions, echo: Echos) {
        if (activePaths == null || activePaths.isEmpty()) {
            activateExistingLocal(options, echo)
        } else {
            activatePaths(activePaths, options, echo)
        }
    }


    private fun Raise<PunktError>.activatePaths(activePaths: Set<Path>, options: ActivateOptions, echo: Echos) {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())
//                .and(FileActiveEqualsLocalFilter.negate())

        val expandedLocalPaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
            acc.addAll(
                activePath.expandToLocal(options.recursive, includeExcludeFilter)
            )
            acc
        }

        commit(expandedLocalPaths, echo)
    }

    private fun Raise<PunktError>.activateExistingLocal(options: ActivateOptions, echo: Echos) {
        val existingLocalPaths = localDirAbsPath.expand(options.recursive, TrueFileFilter.INSTANCE)

        commit(existingLocalPaths, echo)
    }

    private fun commit(localPaths: Set<Path>, echo: Echos) {
        ActiveState.pendingTransactions.addAll(
            localPaths.map { localPath ->
                if (localPath.isDirectory()) {
                    ActiveTransactionMakeDirectories(localPath)
                } else {
                    ActiveTransactionCopyToActive(localPath)
                }
            }
        )

        ActiveState.transact()
    }
}