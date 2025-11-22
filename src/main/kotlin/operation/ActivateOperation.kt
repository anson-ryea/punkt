package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.PunktCommand.Companion.punktYesNoPrompt
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.existsInActive
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.expand
import com.an5on.file.FileUtils.expandToLocal
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.hub.operation.ActivateOperation
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
 * An operation to activate files, linking them from the local repository to their destination in the filesystem.
 *
 * This class orchestrates the `activate` command's core logic. It handles:
 * - Translating active paths to their corresponding paths in the local repository.
 * - Filtering files based on include/exclude patterns and ignore rules.
 * - Creating a set of pending filesystem transactions (e.g., copy to active, create directory).
 * - Displaying the proposed changes to the user and, if configured, prompting for confirmation.
 * - Committing the transactions to apply the changes, creating symbolic links or copying files.
 *
 * It can operate on a specific set of paths or on all files tracked in the local repository.
 *
 * @param activePaths An optional set of paths in the active state to activate. If null, all tracked files are activated.
 * @param globalOptions The global command-line options, influencing verbosity and interactivity.
 * @param commonOptions The common options for filtering (include/exclude) and recursion.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction, such as confirmation prompts.
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
     * Activates a specified set of paths from the local repository to the active state.
     *
     * This method is called when the `activate` command is given specific path arguments. It performs the following:
     * 1.  Ensures that each provided active path exists within the local repository.
     * 2.  Applies include/exclude filters and ignores files that are already identical in the active state.
     * 3.  Expands the given paths to a full list of corresponding files in the local state.
     * 4.  Delegates to the `commit` method to process and apply the changes.
     *
     * @param paths The set of paths in the active state to activate.
     * @return An [Either] containing a [PunktError] on failure (e.g., if a path is not found) or [Unit] on success.
     */
    override fun operateWithPaths(paths: Set<Path>): Either<PunktError, Unit> = either {

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

            activePath.expandToLocal(
                filter.and(ActiveEqualsLocalFileFilter.negate()),
                filter
            ).filterNot {
                it.isDirectory() && it.existsInActive()
            }
        }.toSet()

        commit(expandedLocalPaths, globalOptions, echos, terminal).bind()
    }

    /**
     * Activates all files currently tracked in the local repository.
     *
     * This method is called when the `activate` command is run without any specific path arguments. It performs the following:
     * 1.  Applies include/exclude filters and ignores files that are already identical in the active state.
     * 2.  Traverses the entire local repository to gather a list of all tracked files.
     * 3.  Delegates to the `commit` method to process and apply the changes.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operateWithExistingLocal(): Either<PunktError, Unit> = either {
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
            ).filterNot {
                it.isDirectory() && it.existsInActive()
            }

        commit(existingLocalPaths, globalOptions, echos, terminal).bind()

        ActivateOperation(
            globalOptions,
            echos,
            terminal
        ).run().bind()
    }

    /**
     * Commits a collection of local paths to the active state by creating and executing filesystem transactions.
     *
     * This private helper method orchestrates the final steps of the activation process:
     * 1.  Creates `ActiveTransaction` objects for each local path (e.g., `ActiveTransactionCopyToActive`).
     * 2.  Displays the pending transactions to the user.
     * 3.  If interactivity is enabled, prompts the user for confirmation before proceeding.
     * 4.  If confirmed (or if interactivity is disabled), commits the transactions, applying the changes to the filesystem.
     *
     * @param localPaths The collection of paths in the local repository to be activated.
     * @param globalOptions The global command-line options.
     * @param echos The echo functions for output.
     * @param terminal The terminal instance for user interaction.
     * @return An [Either] containing a [PunktError] on failure (e.g., if the user cancels) or [Unit] on success.
     */
    private fun commit(
        localPaths: Collection<Path>,
        globalOptions: GlobalOptions,
        echos: Echos,
        terminal: Terminal
    ): Either<PunktError, Unit> = either {
        ActiveState.pendingTransactions.addAll(
            localPaths.map { localPath ->
                if (localPath.isDirectory()) {
                    ActiveTransactionMakeDirectories(localPath)
                } else {
                    ActiveTransactionCopyToActive(localPath)
                }
            }
        )

        if (localPaths.isEmpty()) return@either

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