package com.an5on.error

/** Errors related to retrieving or using credentials. */
sealed interface CredentialError : PunktError {
    override val code: String

    data class ManagerNotFound(
        val command: String = "git-credential-manager",
        override val cause: Throwable? = null
    ) : CredentialError {
        override val code: String = "CRED_MANAGER_NOT_FOUND"
        override val message: String
            get() = "Credential manager not found or not executable: $command"
    }

    data class NotConfigured(
        val host: String,
        override val cause: Throwable? = null
    ) : CredentialError {
        override val code: String = "CRED_NOT_CONFIGURED"
        override val message: String
            get() = "No credentials configured for host: $host"
    }

    data class Invalid(
        val reason: String,
        override val cause: Throwable? = null
    ) : CredentialError {
        override val code: String = "CRED_INVALID"
        override val message: String
            get() = "Invalid credentials: $reason"
    }
}
