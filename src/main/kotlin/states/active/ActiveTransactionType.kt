package com.an5on.states.active

/**
 * Enumerates the types of file system operations that can be queued as an [ActiveTransaction].
 *
 * Each value corresponds to a specific action to be performed on the file system's "active" state.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
enum class ActiveTransactionType {
    /**
     * Represents an operation to copy a file from the local state to the active state.
     * This is typically used for creating or updating a file in the active directory.
     */
    COPY_TO_ACTIVE,

    /**
     * Represents an operation to create all necessary parent directories for a path in the active state.
     * This ensures that the directory structure exists before a file is copied.
     */
    MKDIRS,
}