package com.an5on.states.local

/**
 * Represents the types of transactions that can be performed on the local state.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
enum class LocalTransactionType {
    /**
     * Transaction type for copying a file from the active path to the local path.
     */
    COPY_TO_LOCAL,

    /**
     * Transaction type for creating the necessary directories in the local state.
     */
    MKDIRS,

    /**
     * Transaction type for removing a file or directory from the local state.
     */
    REMOVE
}