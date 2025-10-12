package com.an5on.states.active

import java.nio.file.Path

/**
 * Represents a transaction to be performed on the active state.
 *
 * This abstract class defines the structure for transactions that modify the active state of files.
 *
 * @property type the type of the transaction
 * @property localPath the local path associated with the transaction
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
abstract class ActiveTransaction {
    /**
     * The type of the transaction.
     */
    abstract val type: ActiveTransactionType

    /**
     * The local path associated with the transaction.
     */
    abstract val localPath: Path

    /**
     * Executes the transaction.
     */
    abstract fun run()

    /**
     * Checks if this transaction is equal to another object.
     *
     * Two transactions are equal if they have the same type and local path.
     *
     * @param other the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean = if (other is ActiveTransaction) {
        this.type == other.type && this.localPath == other.localPath
    } else {
        false
    }

    /**
     * Returns the hash code for this transaction.
     *
     * The hash code is computed based on the type and local path.
     *
     * @return the hash code value
     */
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + localPath.hashCode()
        return result
    }
}