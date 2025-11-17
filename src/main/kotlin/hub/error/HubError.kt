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

    data class OperationFailed(
        val operation: String,
        val reason: String,
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_OPERATION_FAILED"
        override val message: String
            get() = "${operation.replaceFirstChar { it.uppercase() }} failed: $reason"
    }

    data class LoggedOut(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_LOGGED_OUT"
        override val message: String
            get() = "You are not logged in. Please log in to continue."
    }

    data class AlreadyLoggedIn(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_ALREADY_LOGGED_IN"
        override val message: String
            get() = "You are already logged in."
    }

    data class SessionExpired(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_SESSION_EXPIRED"
        override val message: String
            get() = "Your session has expired. Please login again."
    }
}