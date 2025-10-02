package com.an5on.states.tracked

import kotlinx.serialization.Serializable

@Serializable
data class TrackedEntryDir(
    private val dirMode: UInt
): TrackedEntry(TrackedType.DIR, dirMode)
