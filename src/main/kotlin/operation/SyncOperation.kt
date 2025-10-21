package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.indented
import com.an5on.command.CommandUtils.punktYesNoPrompt
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.file.filter.PunktIgnoreFileFilter
import com.an5on.operation.OperationUtils.executeGitOnLocalChange
import com.an5on.operation.OperationUtils.existingLocalPathsToActivePaths
import com.an5on.operation.OperationUtils.expand
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionMakeDirectories
import com.an5on.type.Interactivity
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import org.apache.commons.io.filefilter.RegexFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

/**
 * Handles the sync operation to synchronize files from the active state to the local state.
 *
 * This object provides operations to sync paths, either by syncing existing local files or specific paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object SyncOperation {
    /**
     * Syncs the specified active paths or all existing local files if no paths are provided.
     *
     * @param activePaths the set of active paths to sync, or null to sync all existing local files
     * @param commonOptions the sync options
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.sync(
        activePaths: Set<Path>?,
        globalOptions: GlobalOptions,
        commonOptions: CommonOptions,
        echos: Echos,
        terminal: Terminal
    ) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            syncExistingLocal(globalOptions, commonOptions, echos, terminal)
        } else {
            syncPaths(activePaths, globalOptions, commonOptions, echos, terminal)
        }

        executeGitOnLocalChange(globalOptions)
    }

    /**
     * Syncs the specified set of active paths.
     *
     * @param activePaths the set of active paths to sync
     * @param commonOptions the sync options
     * @param echos the echo functions for output
     */
    private fun Raise<PunktError>.syncPaths(
        activePaths: Set<Path>,
        globalOptions: GlobalOptions,
        commonOptions: CommonOptions,
        echos: Echos,
        terminal: Terminal
    ) {

        val includeExcludeFilter = RegexFileFilter(commonOptions.include.pattern)
            .and(RegexFileFilter(commonOptions.exclude.pattern).negate())
            .and(DefaultActiveIgnoreFileFilter)
            .and(PunktIgnoreFileFilter)
//            .and(ActiveEqualsLocalFileFilter.negate())

        val expandedActivePaths = activePaths.flatMap { activePath ->
            echos.echoStage(
                "Syncing: $activePath",
                globalOptions.verbosity,
                Verbosity.NORMAL
            )

            activePath.expand(commonOptions.recursive, includeExcludeFilter)
        }.toSet()

        LocalState.pendingTransactions.addAll(
            expandedActivePaths.map { activePath ->
                if (activePath.isDirectory()) {
                    LocalTransactionMakeDirectories(activePath)
                } else {
                    LocalTransactionCopyToLocal(activePath)
                }
            }
        )

        if (LocalState.pendingTransactions.isEmpty()) return

        echos.echoWithVerbosity(
            "The following operations will be performed:".indented(),
            true,
            false,
            globalOptions.verbosity,
            Verbosity.FULL
        )
        LocalState.pendingTransactions.forEach { transaction ->
            echos.echoWithVerbosity(
                "${transaction.type} - ${transaction.activePath}".indented(),
                true,
                false,
                globalOptions.verbosity,
                Verbosity.FULL
            )
        }

        if (globalOptions.interactivity == Interactivity.ALWAYS) {
            if (punktYesNoPrompt(
                    "Do you want to sync ${LocalState.pendingTransactions.size} items?",
                    terminal
                ).ask() != true
            ) {
                LocalState.pendingTransactions.clear()

                raise(PunktError.OperationCancelled("No changes to the filesystem were made"))
            }
        }

        LocalState.commit()
    }

    private fun Raise<PunktError>.syncExistingLocal(
        globalOptions: GlobalOptions,
        commonOptions: CommonOptions,
        echos: Echos,
        terminal: Terminal
    ) {
        syncPaths(existingLocalPathsToActivePaths, globalOptions, commonOptions, echos, terminal)
    }
}