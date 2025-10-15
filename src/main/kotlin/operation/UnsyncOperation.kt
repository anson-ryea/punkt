package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.ECHO_CONTENT_INDENTATION
import com.an5on.command.CommandUtils.determineVerbosity
import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtils.executeGitOnLocalChange
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.type.VerbosityType
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Handles the unsync operation to remove files from the local state.
 *
 * This object provides operations to unsync paths by deleting them from the local state.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object UnsyncOperation {
    /**
     * Unsynchronizes the specified active paths by removing them from the local state.
     *
     * @param activePaths the set of active paths to unsync
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.unsync(
        activePaths: Set<Path>,
        globalOptions: GlobalOptions,
        echos: Echos,
        terminal: Terminal
    ) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val verbosity = determineVerbosity(globalOptions.verbosity)

        val localPaths = activePaths.map { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            echos.echoStage("Unsyncing: ${activePath.pathString}", verbosity, VerbosityType.NORMAL)

            activePath.toLocal()
        }.toSet()

        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        if (LocalState.pendingTransactions.isEmpty()) return

        echos.echoWithVerbosity(
            "The following operations will be performed:".indented(),
            true,
            false,
            verbosity,
            VerbosityType.VERBOSE
        )
        LocalState.pendingTransactions.forEach { transaction ->
            echos.echoWithVerbosity(
                "${transaction.type} - ${transaction.activePath}".indented(),
                true,
                false,
                verbosity,
                VerbosityType.VERBOSE
            )
        }

        if (globalOptions.prompt) {
            if (YesNoPrompt(
                    ECHO_CONTENT_INDENTATION +
                            TextStyles.bold(
                                TextColors.yellow(
                                    "Do you want to unsync ${LocalState.pendingTransactions.size} items?".indented()
                                )
                            ),
                    terminal
                ).ask() != true
            ) {
                LocalState.pendingTransactions.clear()

                raise(PunktError.OperationCancelled("No changes to the filesystem were made"))
            }
        }

        LocalState.commit()

        executeGitOnLocalChange(globalOptions)
    }
}