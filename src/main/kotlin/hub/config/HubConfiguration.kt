package com.an5on.hub.config

import kotlinx.serialization.Serializable

@Serializable
data class HubConfiguration (
    val serverUrl: String = "https://dot-backend-85b8.onrender.com"
)