package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.Echos
import com.an5on.command.PunktCommand.Companion.punktYesNoPrompt
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.command.options.SyncOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.expand
import com.an5on.file.FileUtils.expandToActive
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.ActiveEqualsLocalFileFilter
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.PunktIgnoreFileFilter
import com.an5on.operation.Operable.Companion.executeGitOnLocalChange
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionCopyToLocal
import com.an5on.states.local.LocalTransactionKeepDirectory
import com.an5on.type.Interactivity
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import org.apache.commons.io.file.PathUtils.isEmpty
import org.apache.commons.io.filefilter.RegexFileFilter
import java.nio.file.Path
import kotlin.io.path.isDirectory

/**
 * An operation to synchronise files from the active state (user's filesystem) to the local state (`punkt` repository).
 *
 * This class orchestrates the `sync` command's core logic. It handles:
 * - Expanding user-provided paths into a full list of files and directories.
 * - Filtering files based on include/exclude patterns and ignore rules.
 * - Creating a set of pending filesystem transactions (e.g., copy to local, create directory).
 * - Displaying proposed changes to the user and, if configured, prompting for confirmation.
 * - Committing the transactions to apply the changes.
 * - Triggering post-sync Git operations like `add` and `commit`.
 *
 * It can operate on a specific set of paths or on all files currently tracked in the local repository.
 *
 * @param activePaths An optional set of paths in the active state to synchronise. If null, all tracked files are synchronised.
 * @param globalOptions The global command-line options, influencing verbosity, interactivity, and Git actions.
 * @param commonOptions The common options for filtering (include/exclude) and recursion.
 * @param syncOptions The options specific to the sync command, such as whether to preserve empty folders.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction, such as confirmation prompts.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class SyncOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    syncOptions: SyncOptions,
    echos: Echos,
    terminal: Terminal,
) : OperableWithPathsAndExistingLocal(
    activePaths,
    globalOptions,
    commonOptions,
    syncOptions,
    echos,
    terminal
) {
    private val syncOptions = specificOptions as SyncOptions
    private val filter = RegexFileFilter(commonOptions.include.pattern)
        .and(RegexFileFilter(commonOptions.exclude.pattern).negate())
        .and(DefaultActiveIgnoreFileFilter)
        .and(PunktIgnoreFileFilter)

    /**
     * Executes the sync operation for a specified set of paths from the active state.
     *
     * This method performs the following steps:
     * 1.  Expands the input `paths` into a list of individual files and directories, applying configured filters.
     * 2.  Creates a `LocalTransaction` for each item (e.g., `LocalTransactionCopyToLocal`).
     * 3.  Displays the pending transactions to the user.
     * 4.  If interactivity is enabled, prompts the user for confirmation before proceeding.
     * 5.  If confirmed, commits the transactions, copying the files to the local state.
     *
     * @param paths The set of paths in the active state to synchronise.
     * @return An [Either] containing a [PunktError] on failure (e.g., if cancelled) or [Unit] on success.
     */
    override fun operateWithPaths(paths: Set<Path>) = either<PunktError, Unit> {
        val expandedActivePaths = paths.flatMap { activePath ->
            echos.echoStage(
                "Syncing: ${activePath.toStringInPathStyle(globalOptions.pathStyle)}",
                globalOptions.verbosity,
                Verbosity.NORMAL
            )
            activePath.expand(
                filter.and(ActiveEqualsLocalFileFilter.negate()),
                if (commonOptions.recursive) filter else null,
                !syncOptions.keepEmptyFolders
            )
        }.toSet()

        LocalState.pendingTransactions.addAll(
            expandedActivePaths.map { activePath ->
                if (syncOptions.keepEmptyFolders && activePath.isDirectory() && isEmpty(activePath)) {
                    LocalTransactionKeepDirectory(activePath)
                } else {
                    LocalTransactionCopyToLocal(activePath)
                }
            }
        )

        if (LocalState.pendingTransactions.isEmpty()) {
            return Either.Right(Unit)
        }

        LocalState.echoPendingTransactions(globalOptions.verbosity, echos)

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

    /**
     * Executes the sync operation for all files currently tracked in the local state.
     *
     * This method is called when the `sync` command is run without specific path arguments. It identifies all
     * files in the local repository, finds their corresponding paths in the active state, and then calls
     * [operateWithPaths] to re-synchronise them.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operateWithExistingLocal() = either<PunktError, Unit> {
        operateWithPaths(
            configuration.global.localStatePath.expandToActive(
                filter.and(DefaultLocalIgnoreFileFilter),
                filesOnly = true,
            )
        )
    }

    /**
     * A hook that runs after a successful sync operation to perform Git-related actions.
     *
     * Based on the `gitOnLocalChange` configuration, this method will automatically stage (`git add`),
     * commit, and/or push the changes made to the local repository.
     *
     * @return An [Either] containing a [PunktError] if the Git operation fails, or [Unit] on success.
     */
    override fun runAfter() = either<PunktError, Unit> {
        echos.echoStage(
            "Executing Git operations: ${configuration.git.gitOnLocalChange}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
        executeGitOnLocalChange(globalOptions, this@SyncOperation).bind()
    }
}