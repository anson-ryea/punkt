package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UploadDotfilePayload (
    @SerialName("collection_id")
    val collectionId: Int,
    val content: List<Dotfile>
)