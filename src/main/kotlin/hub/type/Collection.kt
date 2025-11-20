package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Serializable
data class Collection @OptIn(ExperimentalTime::class) constructor(
    val id: Int,
    val name: String,
    val description: String?,
    @SerialName("created_at")
    val createdAt: Instant,
    @SerialName("updated_at")
    val updatedAt: Instant
)