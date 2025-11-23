package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single dotfile entry in a collection.
 *
 * @property pathname Absolute path of the dotfile on the local system.
 * @property fileName Name of the file, without directory components.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class Dotfile(
    @SerialName("path")
    val pathname: String,
    @SerialName("filename")
    val fileName: String,
)