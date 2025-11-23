package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Request payload used to upload one or more dotfiles to a collection.
 *
 * @property collectionId Identifier of the target collection that will receive the dotfiles.
 * @property content List of dotfiles and their metadata to be uploaded.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class UploadDotfilePayload (
    @SerialName("collection_id")
    val collectionId: Int,
    val content: List<Dotfile>
)