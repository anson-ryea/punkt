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

    data class InvalidRemote(
        val repo: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_INVALID_REMOTE"
        override val message: String
            get() = "Invalid remote repository URL: $repo"
    }

    data class GcmNotSet(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_GCM_NOT_SET"
        override val message: String
            get() = "Git Credential Manager (GCM) is not installed or not configured"
    }
}
