package com.an5on.states.local

/**
 * Defines the set of possible operations that can be applied to the local state as part of a [LocalTransaction].
 *
 * Each type corresponds to a specific action, such as adding, removing, or preserving files and directories within
 * the local `.punkt` repository.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
enum class LocalTransactionType {
    /**
     * An operation to copy a file from the active working directory to the local state.
     * This is typically used when a new file is added or an existing file is modified.
     */
    COPY_TO_LOCAL,

    /**
     * An operation to preserve an empty directory in the local state.
     * This is necessary because version control systems like Git do not track empty directories, so a marker file
     * is created to ensure the directory is maintained.
     */
    KEEP_DIRECTORY,

    /**
     * An operation to delete a file or directory from the local state.
     * This is used when a file or directory is removed from the active working area and needs to be mirrored in the
     * local repository.
     */
    REMOVE,
}