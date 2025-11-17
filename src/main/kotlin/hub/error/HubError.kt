package com.an5on.hub.error

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError

sealed interface HubError : PunktError {
    override val code: String

    data class ServerTimeout(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_SERVER_TIMEOUT"
        override val message: String
            get() = "Unable to connect to Hub server: ${configuration.hub.serverUrl}"
    }

    data class RegisterFailed(
        val reason: String,
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_REGISTER_FAILED"
        override val message: String
            get() = "Registration failed: $reason"
    }
}