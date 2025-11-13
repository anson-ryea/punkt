package com.an5on.states.active

import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.type.Verbosity

/**
 * Manages the files in active state in the Punkt system.
 *
 * This object handles transactions and operations related to the active state, such as copying files and creating directories.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ActiveState {
    /**
     * A set of pending transactions to be executed.
     */
    val pendingTransactions = mutableSetOf<ActiveTransaction>()

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
                "${transaction.type} - ${transaction.localPath}".indented(),
                true,
                false,
                verbosity,
                Verbosity.FULL
            )
        }
    }

    /**
     * Executes all pending transactions.
     */
    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }
}