package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response payload returned by the hub authentication endpoint.
 *
 * @property accessToken Bearer access token used to authenticate subsequent requests.
 * @property tokenType Type of the token, typically `Bearer`.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class TokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String,
)