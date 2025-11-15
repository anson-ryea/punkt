package com.an5on.states.active

import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.type.Verbosity

/**
 * Manages and orchestrates file operations within the "active" state of the Punkt system.
 *
 * This object serves as a central point for handling transactions that modify files in the active
 * state, such as copying, updating, or deleting them. It collects proposed changes as a set of
 * [ActiveTransaction] objects, which can then be reviewed and committed.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object ActiveState {
    /**
     * A mutable set of [ActiveTransaction] objects waiting to be executed.
     *
     * Transactions are added here to be performed later when [commit] is called.
     */
    val pendingTransactions = mutableSetOf<ActiveTransaction>()

    /**
     * Displays a summary of all pending transactions based on the specified verbosity level.
     *
     * This function is intended for providing a dry-run or preview of the changes that will be
     * made when [commit] is called.
     *
     * @param verbosity The verbosity level controlling whether the output is shown.
     * @param echos The [Echos] instance used for printing messages to the console.
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
                "${transaction.type} - ${transaction.localPath}".indented(),
                true,
                false,
                verbosity,
                Verbosity.FULL
            )
        }
    }

    /**
     * Executes all transactions currently held in [pendingTransactions].
     *
     * This method iterates through the collected transactions and runs each one, applying the
     * file system changes. The set of pending transactions is not cleared automatically.
     */
    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }
}