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
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.toLocal
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.operation.Operable.Companion.executeGitOnLocalChange
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.type.Interactivity
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

/**
 * An operation to remove files from the `punkt` local repository, effectively "unsyncing" them.
 *
 * This class orchestrates the `unsync` command's core logic. It handles:
 * - Verifying that the specified files exist in the local repository.
 * - Creating a set of pending delete transactions.
 * - Displaying the proposed deletions to the user and, if configured, prompting for confirmation.
 * - Committing the transactions to remove the files from the local state.
 * - Triggering post-unsync Git operations like `add` and `commit` to record the removal.
 *
 * @param activePaths The set of paths in the active state to remove from the local repository.
 * @param globalOptions The global command-line options, influencing verbosity, interactivity, and Git actions.
 * @param commonOptions The common options for filtering and recursion.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction, such as confirmation prompts.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class UnsyncOperation(
    private val activePaths: Set<Path>,
    private val globalOptions: GlobalOptions,
    private val commonOptions: CommonOptions,
    private val echos: Echos,
    private val terminal: Terminal,
) : Operable {
    /**
     * Executes the unsync operation.
     *
     * This method performs the following steps:
     * 1.  Ensures the local repository exists.
     * 2.  Verifies that each target path exists in the local repository.
     * 3.  Creates a `LocalTransactionDelete` for each target.
     * 4.  Displays the pending transactions to the user.
     * 5.  If interactivity is enabled, prompts the user for confirmation.
     * 6.  If confirmed, commits the transactions, deleting the files from the local state.
     *
     * @return An [Either] containing a [PunktError] on failure (e.g., if a path is not found or the operation is cancelled) or [Unit] on success.
     */
    override fun operate() = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val localPaths = activePaths.map { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            echos.echoStage(
                "Unsyncing: ${activePath.toStringInPathStyle(globalOptions.pathStyle)}",
                globalOptions.verbosity,
                Verbosity.NORMAL
            )

            activePath.toLocal()
        }.toSet()

        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        if (LocalState.pendingTransactions.isEmpty()) {
            return Either.Right(Unit)
        }

        LocalState.echoPendingTransactions(globalOptions.verbosity, echos)

        if (globalOptions.interactivity == Interactivity.ALWAYS) {
            if (punktYesNoPrompt(
                    "Do you want to unsync ${LocalState.pendingTransactions.size} items?",
                    terminal
                ).ask() != true
            ) {
                LocalState.pendingTransactions.clear()

                raise(PunktError.OperationCancelled("No changes to the filesystem were made"))
            }
        }

        LocalState.commit()
    }

    /**
     * A hook that runs after a successful unsync operation to perform Git-related actions.
     *
     * Based on the `gitOnLocalChange` configuration, this method will automatically stage (`git add`) the deletions,
     * commit, and/or push the changes made to the local repository.
     *
     * @return An [Either] containing a [PunktError] if the Git operation fails, or [Unit] on success.
     */
    override fun runAfter() = either {
        echos.echoStage(
            "Executing Git operations: ${configuration.git.gitOnLocalChange}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
        executeGitOnLocalChange(globalOptions, this@UnsyncOperation).bind()
    }
}