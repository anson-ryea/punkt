package com.an5on.states.tracked

import kotlinx.serialization.Serializable

@Serializable
sealed class TrackedEntry(
    val type: TrackedType,
    open val mode: UInt
)
