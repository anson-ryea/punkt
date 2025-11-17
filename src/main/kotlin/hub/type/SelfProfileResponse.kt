package com.an5on.hub.type

import kotlinx.serialization.Serializable

@Serializable
data class SelfProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
)