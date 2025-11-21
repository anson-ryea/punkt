package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateCollectionRequest(
    val name: String,
    val description: String,
    @SerialName("is_private")
    val isPrivate: Boolean
)