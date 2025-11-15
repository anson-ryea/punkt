package com.an5on.states.local

import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.local.LocalState.pendingTransactions
import com.an5on.type.Verbosity
import kotlin.io.path.exists

/**
 * Manages the local state of files within the Punkt system.
 *
 * This object is responsible for handling operations concerning the local state,
 * which includes committing transactions and manipulating files in the local repository.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object LocalState {
    /**
     * Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    fun exists() = configuration.global.localStatePath.exists()

    /**
     * A mutable set of pending [LocalTransaction] instances that are ready to be committed.
     */
    val pendingTransactions = mutableSetOf<LocalTransaction>()

    /**
     * Echoes the pending transactions to the console based on the specified verbosity level.
     *
     * @param verbosity The verbosity level that determines whether the messages are displayed.
     * @param echos The [Echos] instance used for outputting messages.
     */
    fun echoPendingTransactions(verbosity: Verbosity, echos: Echos) {
        echos.echoWithVerbosity(
            "The following operations will be performed:".indented(),
            true,
            false,
            verbosity,
            Verbosity.FULL
        )

        pendingTransactions.forEach { transaction ->
            echos.echoWithVerbosity(
                "${transaction.type} - ${transaction.activePath}".indented(),
                true,
                false,
                verbosity,
                Verbosity.FULL
            )
        }
    }

    /**
     * Executes all pending transactions in the [pendingTransactions] set.
     */
    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }
}