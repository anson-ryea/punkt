package com.an5on.states.local

import java.nio.file.Path

/**
 * Represents a transaction to be performed on the local state.
 *
 * This abstract class defines the structure for transactions that modify the local state of files.
 *
 * @property type the type of the transaction
 * @property activePath the active path associated with the transaction
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
abstract class LocalTransaction {
    /**
     * The type of the transaction.
     */
    abstract val type: LocalTransactionType

    /**
     * The active path associated with the transaction.
     */
    abstract val activePath: Path

    /**
     * Executes the transaction.
     */
    abstract fun run()

    /**
     * Checks if this transaction is equal to another object.
     *
     * Two transactions are equal if they have the same type and active path.
     *
     * @param other the object to compare with
     * @return true if the objects are equal, false otherwise
     */
    override fun equals(other: Any?): Boolean = if (other is LocalTransaction) {
        this.type == other.type && this.activePath == other.activePath
    } else {
        false
    }

    /**
     * Returns the hash code for this transaction.
     *
     * The hash code is computed based on the type and active path.
     *
     * @return the hash code value
     */
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + activePath.hashCode()
        return result
    }
}