package com.an5on.states.tracked

import kotlinx.serialization.Serializable

@Serializable
data class TrackedEntryFile(
    private val fileMode: UInt,
    val contentHash: String,
): TrackedEntry(TrackedType.FILE, fileMode)
