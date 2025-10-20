package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.determineVerbosity
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.file.filter.DefaultIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveState
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.type.Verbosity
import java.nio.file.Path
import kotlin.io.path.isDirectory

/**
 * Handles the activation of files from the local state to the active state.
 *
 * This object provides operations to activate paths, either by activating existing local files or specific paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ActivateOperation {
    /**
     * Activates the specified active paths or all existing local files if no paths are provided.
     *
     * @param activePaths the set of active paths to activate, or null to activate all existing local files
     * @param options the activation options
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.activate(
        activePaths: Set<Path>?,
        globalOptions: GlobalOptions,
        commonOptions: CommonOptions,
        echos: Echos
    ) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            activateExistingLocal(globalOptions, commonOptions, echos)
        } else {
            activatePaths(activePaths, globalOptions, commonOptions, echos)
        }
    }

    /**
     * Activates the specified set of active paths.
     *
     * @param activePaths the set of active paths to activate
     * @param commonOptions the activation options
     * @param echos the echo functions for output
     */
    private fun Raise<PunktError>.activatePaths(
        activePaths: Set<Path>,
        globalOptions: GlobalOptions,
        commonOptions: CommonOptions,
        echos: Echos
    ) {
        val verbosity = determineVerbosity(globalOptions.verbosity)

        val includeExcludeFilter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultIgnoreFileFilter)
            .and(ActiveEqualsLocalFileFilter.negate())

        val expandedLocalPaths = activePaths.flatMap { activePath ->
            echos.echoStage("Activating: $activePath", verbosity, Verbosity.NORMAL)

            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(commonOptions.recursive, includeExcludeFilter)
        }.toSet()

        commit(expandedLocalPaths, echos)
    }

    private fun activateExistingLocal(globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        val verbosity = determineVerbosity(globalOptions.verbosity)

        echos.echoStage("Activating: existing synced local files", verbosity, Verbosity.NORMAL)

        val existingLocalPaths =
            configuration.global.localStatePath.expand(commonOptions.recursive, DefaultIgnoreFileFilter)

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