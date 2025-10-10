package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.options.ActivateOptions
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveState
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.states.local.LocalState
import com.an5on.command.Echos
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

object ActivateOperation {
    fun activate(activePaths: Set<Path>?, options: ActivateOptions, echo: Echos): Either<PunktError, Unit> =
        either {
            if (activePaths == null || activePaths.isEmpty()) {
                activateExistingLocal(options, echo).bind()
            } else {
                activatePaths(activePaths, options, echo).bind()
            }
        }


    private fun activatePaths(activePaths: Set<Path>, options: ActivateOptions, echo: Echos): Either<PunktError, Unit> =
        either {

            ensure(LocalState.exists()) {
                LocalError.LocalNotFound()
            }

            val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
                .and(RegexBasedOnActiveFileFilter(options.exclude).negate())
//                .and(FileActiveEqualsLocalFilter.negate())

            val expandedLocalPaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
                acc.addAll(
                    activePath.expandToLocal(options.recursive, includeExcludeFilter).bind()
                )
                acc
            }

            commit(expandedLocalPaths, echo).bind()
        }

    private fun activateExistingLocal(options: ActivateOptions, echo: Echos): Either<PunktError, Unit> = either {
        val existingLocalPaths = localDirAbsPath.expand(options.recursive, TrueFileFilter.INSTANCE).bind()

        commit(existingLocalPaths, echo).bind()
    }

    private fun commit(localPaths: Set<Path>, echo: Echos): Either<PunktError, Unit> = either {
        ActiveState.pendingTransactions.addAll(
            localPaths.map { localPath ->
                if (localPath.isDirectory()) {
                    ActiveTransactionMakeDirectories(localPath)
                } else {
                    ActiveTransactionCopyToActive(localPath)
                }
            }
        )

        ActiveState.transact().bind()
    }
}