package com.an5on.states.tracked

import kotlinx.serialization.Serializable

@Serializable
data class TrackedEntryDir(
    val placeholder: Int = 0
) : TrackedEntry(TrackedType.DIR)
