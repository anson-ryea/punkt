package com.an5on.type

/**
 * An enumeration representing Git operations to be taken on local state changes.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
enum class GitOnLocalChange {
    NONE,
    ADD,
    COMMIT,
    ADD_COMMIT,
    COMMIT_PUSH,
    ADD_COMMIT_PUSH,
}