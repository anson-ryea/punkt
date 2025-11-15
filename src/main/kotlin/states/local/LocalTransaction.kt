package com.an5on.states.local

import java.nio.file.Path

/**
 * Represents a transaction to be performed on the local state.
 *
 * This abstract class serves as a blueprint for operations that modify the local file state within the Punkt system.
 * Each transaction encapsulates a specific action, such as adding, updating, or removing a file, and targets a particular
 * path in the active working directory.
 *
 * Subclasses must implement the [run] method to define the concrete logic for the transaction. Equality and hash code
 * are determined by the transaction's [type] and [activePath], ensuring that transactions can be uniquely identified.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
abstract class LocalTransaction(
    open val type: LocalTransactionType,
    open val activePath: Path
) {
    /**
     * Executes the transaction, applying the defined operation to the local state.
     */
    abstract fun run()

    /**
     * Determines whether this transaction is equal to another object.
     *
     * Two [LocalTransaction] instances are considered equal if they share the same [type] and [activePath].
     *
     * @param other The object to compare against this transaction.
     * @return `true` if the objects are equal, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean = if (other is LocalTransaction) {
        this.type == other.type && this.activePath == other.activePath
    } else {
        false
    }

    /**
     * Computes the hash code for this transaction.
     *
     * The hash code is derived from the [type] and [activePath] to ensure consistency with the [equals] method.
     *
     * @return The hash code value for this transaction.
     */
    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + activePath.hashCode()
        return result
    }
}