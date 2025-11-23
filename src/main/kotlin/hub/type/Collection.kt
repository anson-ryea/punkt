package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Represents a collection of dotfiles stored in the hub.
 *
 * @property id Unique identifier of the collection.
 * @property name Human-readable name of the collection.
 * @property description Optional free-form description of the collection.
 * @property createdAt Timestamp when the collection was initially created.
 * @property updatedAt Timestamp when the collection was last modified.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
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