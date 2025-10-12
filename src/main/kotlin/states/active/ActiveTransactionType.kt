package com.an5on.states.active

/**
 * Represents the types of transactions that can be performed on the active state.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
enum class ActiveTransactionType {
    /**
     * Transaction type for copying a file from the local path to the active path.
     */
    COPY_TO_ACTIVE,

    /**
     * Transaction type for creating the necessary directories in the active state.
     */
    MKDIRS,
}