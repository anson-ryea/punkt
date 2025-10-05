package com.an5on.error

/** Git/JGit-related errors. */
sealed interface GitError : PunktError {
    override val code: String

    data class InitFailed(
        val directory: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_INIT_FAILED"
        override val message: String
            get() = "Failed to initialise Git repository at $directory"
    }

    data class CloneFailed(
        val uri: String,
        val targetDir: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_CLONE_FAILED"
        override val message: String
            get() = "Failed to clone $uri to $targetDir"
    }

    data class RepoNotFound(
        val uri: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_REPO_NOT_FOUND"
        override val message: String
            get() = "Remote repository not found: $uri"
    }

    data class InvalidRef(
        val ref: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_INVALID_REF"
        override val message: String
            get() = "Invalid Git reference/branch: $ref"
    }

    data class Transport(
        val uri: String,
        override val cause: Throwable
    ) : GitError {
        override val code: String = "GIT_TRANSPORT_ERROR"
        override val message: String
            get() = "Git transport error while accessing $uri: ${cause.message ?: cause::class.simpleName}"
    }

    data class AuthRequired(
        val uri: String
    ) : GitError {
        override val code: String = "GIT_AUTH_REQUIRED"
        override val message: String
            get() = "Authentication required for $uri"
        override val cause: Throwable? = null
    }

    data class AuthFailed(
        val uri: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_AUTH_FAILED"
        override val message: String
            get() = "Authentication failed for $uri"
    }

    data class SshHostKeyUnknown(
        val host: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SSH_HOSTKEY_UNKNOWN"
        override val message: String
            get() = "Unknown SSH host key for $host"
    }

    data class SshIdentityNotFound(
        val identityPath: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SSH_IDENTITY_NOT_FOUND"
        override val message: String
            get() = "SSH identity not found: $identityPath"
    }

    data class CredentialsNotFound(
        val uri: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_CREDENTIALS_NOT_FOUND"
        override val message: String
            get() = "No stored credentials found for $uri"
    }

    data class GcmNotSet(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_GCM_NOT_SET"
        override val message: String
            get() = "Git Credential Manager (GCM) is not installed or not configured"
    }
}
