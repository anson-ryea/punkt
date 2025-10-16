package com.an5on.type

import kotlinx.serialization.Serializable

@Serializable
enum class GitOnLocalChangeType {
    NONE,
    ADD,
    COMMIT,
    ADD_COMMIT,
    COMMIT_PUSH,
    ADD_COMMIT_PUSH,
}