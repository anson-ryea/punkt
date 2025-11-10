package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.CommandUtils.indented
import com.an5on.command.CommandUtils.punktYesNoPrompt
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtils.executeGitOnLocalChange
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.type.Interactivity
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
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
class UnsyncOperation(
    private val activePaths: Set<Path>,
    private val globalOptions: GlobalOptions,
    private val commonOptions: CommonOptions,
    private val echos: Echos,
    private val terminal: Terminal,
) : Operable {
    /**
     * Unsynchronizes the specified active paths by removing them from the local state.
     *
     * @param activePaths the set of active paths to unsync
     * @param echos the echo functions for output
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
                "Unsyncing: ${activePath.pathString}",
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
                    "Do you want to unsync ${LocalState.pendingTransactions.size} items?",
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