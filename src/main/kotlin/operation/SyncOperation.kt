package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.CommandUtils.punktYesNoPrompt
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.command.options.SyncOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.expand
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
 * Handles the sync operation to synchronize files from the active state to the local state.
 *
 * This object provides operations to sync paths, either by syncing existing local files or specific paths.
 *
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
) : OperableWithBothPathsAndExistingLocal(
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
     * Syncs the specified set of active paths.
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

    override fun operateWithExistingLocal() = either<PunktError, Unit> {
        operateWithPaths(
            configuration.global.localStatePath.expand(
                filter.and(DefaultLocalIgnoreFileFilter),
                filesOnly = true,
            )
        )
    }

    override fun runAfter() = either<PunktError, Unit> {
        echos.echoStage(
            "Executing Git operations: ${configuration.git.gitOnLocalChange}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
        executeGitOnLocalChange(globalOptions, this@SyncOperation).bind()
    }
}