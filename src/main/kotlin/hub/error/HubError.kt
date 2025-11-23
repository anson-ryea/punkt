package com.an5on.hub.error

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError

/**
 * Represents all hub-specific errors raised by hub operations.
 *
 * Each implementation provides a stable error [code] and
 * human-readable [PunktError.message] for display.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
sealed interface HubError : PunktError {
    override val code: String

    /**
     * Error indicating that the hub server could not be reached within the timeout.
     *
     * The error message includes the configured hub server URL.
     *
     * @property cause Optional underlying exception, if any.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class ServerTimeout(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_SERVER_TIMEOUT"
        override val message: String
            get() = "Unable to connect to Hub server: ${configuration.hub.serverUrl}"
    }

    /**
     * Error indicating that a specific hub operation has failed.
     *
     * @property operation Name or description of the operation that failed.
     * @property reason Human-readable reason explaining why the operation failed.
     * @property cause Optional underlying exception, if any.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class OperationFailed(
        val operation: String,
        val reason: String,
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_OPERATION_FAILED"
        override val message: String
            get() = "${operation.replaceFirstChar { it.uppercase() }} failed: $reason"
    }

    /**
     * Error indicating that the user is not currently authenticated with the hub.
     *
     * Typically thrown when an operation requires an active session.
     *
     * @property cause Optional underlying exception, if any.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class LoggedOut(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_LOGGED_OUT"
        override val message: String
            get() = "You are not logged in. Please log in to continue."
    }

    /**
     * Error indicating that a login was attempted whilst the user is already logged in.
     *
     * @property cause Optional underlying exception, if any.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class AlreadyLoggedIn(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_ALREADY_LOGGED_IN"
        override val message: String
            get() = "You are already logged in."
    }

    /**
     * Error indicating that the current authentication session has expired.
     *
     * This usually requires the user to authenticate again.
     *
     * @property cause Optional underlying exception, if any.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class SessionExpired(
        override val cause: Throwable? = null
    ) : HubError {
        override val code: String = "HUB_SESSION_EXPIRED"
        override val message: String
            get() = "Your session has expired. Please login again."
    }
}