package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request payload used to create a new collection in the hub.
 *
 * @property name Desired name of the new collection.
 * @property description Human-readable description of the collection.
 * @property isPrivate Whether the collection should be private and hidden from other users.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class CreateCollectionRequest(
    val name: String,
    val description: String,
    @SerialName("is_private")
    val isPrivate: Boolean
)