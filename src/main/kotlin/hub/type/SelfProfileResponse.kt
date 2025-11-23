package com.an5on.hub.type

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response payload containing details of the authenticated user's profile.
 *
 * @property id Unique identifier of the user.
 * @property username Chosen username of the account.
 * @property email E-mail address associated with the account.
 * @property tier Name of the account tier, such as free or premium.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class SelfProfileResponse(
    val id: Int,
    val username: String,
    val email: String,
    @SerialName("account_tier")
    val tier: String,
)