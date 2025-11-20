package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Dotfile(
    @SerialName("path")
    val pathname: String,
    @SerialName("filename")
    val fileName: String,
)