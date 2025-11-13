package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.punktYesNoPrompt
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.expand
import com.an5on.file.FileUtils.expandToLocal
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.states.active.ActiveState
import com.an5on.states.active.ActiveTransactionCopyToActive
import com.an5on.states.active.ActiveTransactionMakeDirectories
import com.an5on.type.Interactivity
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.terminal.Terminal
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
class ActivateOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    echos: Echos,
    terminal: Terminal
) : OperableWithPathsAndExistingLocal(
    activePaths,
    globalOptions,
    commonOptions,
    OptionGroup(),
    echos,
    terminal
) {
    /**
     * Activates the specified set of active paths.
     *
     * @param activePaths the set of active paths to activate
     * @param commonOptions the activation options
     * @param echos the echo functions for output
     */
    override fun operateWithPaths(paths: Set<Path>) = either {

        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())

        val expandedLocalPaths = paths.flatMap { activePath ->
            echos.echoStage(
                "Activating: ${activePath.toStringInPathStyle(globalOptions.pathStyle)}",
                globalOptions.verbosity,
                Verbosity.NORMAL
            )

            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(filter.and(ActiveEqualsLocalFileFilter.negate()), filter)
        }.toSet()

        commit(expandedLocalPaths, globalOptions, echos, terminal)
    }

    override fun operateWithExistingLocal() = either {
        echos.echoStage(
            "Activating: existing synced local files",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )

        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultLocalIgnoreFileFilter)

        val existingLocalPaths =
            configuration.global.localStatePath.expand(
                filter.and(ActiveEqualsLocalFileFilter.negate()),
                filter
            )

        commit(existingLocalPaths, globalOptions, echos, terminal)
    }

    private fun Raise<PunktError>.commit(
        localPaths: Collection<Path>,
        globalOptions: GlobalOptions,
        echos: Echos,
        terminal: Terminal
    ) {
        ActiveState.pendingTransactions.addAll(
            localPaths.map { localPath ->
                if (localPath.isDirectory()) {
                    ActiveTransactionMakeDirectories(localPath)
                } else {
                    ActiveTransactionCopyToActive(localPath)
                }
            }
        )

        if (localPaths.isEmpty()) return

        ActiveState.echoPendingTransactions(globalOptions.verbosity, echos)

        if (globalOptions.interactivity == Interactivity.ALWAYS) {
            if (punktYesNoPrompt(
                    "Do you want to activate ${ActiveState.pendingTransactions.size} items?",
                    terminal
                ).ask() != true
            ) {
                ActiveState.pendingTransactions.clear()

                raise(PunktError.OperationCancelled("No changes to the filesystem were made"))
            }
        }

        ActiveState.commit()
    }
}