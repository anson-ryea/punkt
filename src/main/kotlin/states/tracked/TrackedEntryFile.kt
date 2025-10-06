package com.an5on.states.tracked

import kotlinx.serialization.Serializable
import java.nio.file.attribute.FileTime

@Serializable
data class TrackedEntryFile(
    val activeLastModified: Long,
    val contentHash: String,
): TrackedEntry(TrackedType.FILE)
