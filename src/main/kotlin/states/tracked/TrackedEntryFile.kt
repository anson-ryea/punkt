package com.an5on.states.tracked

import kotlinx.serialization.Serializable

@Serializable
data class TrackedEntryFile(
    val activeLastModified: Long,
    val contentHash: String,
) : TrackedEntry(TrackedType.FILE)
