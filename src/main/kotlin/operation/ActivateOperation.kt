package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.ActivateOptions
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveState
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalUtils.existsInLocal
import org.apache.commons.io.filefilter.TrueFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

object ActivateOperation {
    fun Raise<PunktError>.activate(activePaths: Set<Path>?, options: ActivateOptions, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            activateExistingLocal(options, echos)
        } else {
            activatePaths(activePaths, options, echos)
        }
    }


    private fun Raise<PunktError>.activatePaths(activePaths: Set<Path>, options: ActivateOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())
            .and(ActiveEqualsLocalFileFilter.negate())

        val expandedLocalPaths = activePaths.flatMap { activePath ->
            echos.echoStage("Activating: $activePath")

            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(options.recursive, includeExcludeFilter)
        }.toSet()

        commit(expandedLocalPaths, echos)
    }

    private fun activateExistingLocal(options: ActivateOptions, echos: Echos) {
        val existingLocalPaths = localDirAbsPath.expand(options.recursive, TrueFileFilter.INSTANCE)

        commit(existingLocalPaths, echos)
    }

    private fun commit(localPaths: Set<Path>, echos: Echos) {
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