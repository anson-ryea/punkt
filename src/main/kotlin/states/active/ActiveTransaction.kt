package com.an5on.states.active

import java.nio.file.Path

/**
 * Represents a single, atomic file operation to be performed within the "active" state.
 *
 * This class acts as a command pattern, encapsulating all the information needed to execute
 * a specific change, such as creating, updating, or deleting a file. Transactions are designed
 * to be collected and then executed in a batch.
 *
 * Equality is based on the transaction [type] and the target [localPath].
 *
 * @property type The [ActiveTransactionType] that defines the nature of the operation (e.g., COPY, DELETE).
 * @property localPath The path in the local state that this transaction targets. The corresponding active path is derived from this.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
abstract class ActiveTransaction(
    open val type: ActiveTransactionType,
    open val localPath: Path
) {
    /**
     * Executes the transaction, performing the file system operation.
     *
     * This method should contain the logic to apply the change represented by this transaction.
     */
    abstract fun run()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ActiveTransaction

        if (type != other.type) return false
        if (localPath != other.localPath) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + localPath.hashCode()
        return result
    }
}