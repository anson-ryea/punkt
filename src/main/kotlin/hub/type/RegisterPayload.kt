package com.an5on.hub.type

import kotlinx.serialization.Serializable

@Serializable
data class RegisterPayload (
    val username: String,
    val email: String,
    val password: String
)