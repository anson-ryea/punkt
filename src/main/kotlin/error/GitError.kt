package com.an5on.error

import com.an5on.git.bundled.BundledGitCredentialsProviderType
import com.an5on.config.ActiveConfiguration.configuration
import java.nio.file.Path
import kotlin.io.path.pathString

/** Git/JGit-related errors. */
sealed interface GitError : PunktError {
    override val code: String

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

    data class EnvCredentialsNotSet(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_ENV_CREDENTIALS_NOT_SET"
        override val message: String
            get() = "Environment variables GIT_USERNAME and GIT_PASSWORD are not set"
    }

    data class GhCliAuthNotSet(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_GH_CLI_AUTH_NOT_SET"
        override val message: String
            get() = "GitHub CLI is not installed or not authenticated"
    }

    data class BundledCredentialsNotFound(
        val methods: Collection<BundledGitCredentialsProviderType>,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_BUNDLED_CREDENTIALS_NOT_FOUND"
        override val message: String
            get() = "No credentials found for bundled Git using the configured methods: ${methods.joinToString(", ")}"
    }

    data class SystemGitNotFound(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SYSTEM_GIT_NOT_FOUND"
        override val message: String
            get() = "System Git is not installed or not found in PATH"
    }

    data class InitFailed(
        val path: Path = configuration.global.localStatePath,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_INIT_FAILED"
        override val message: String
            get() = "Failed to initialise git repository at ${path.pathString}"
    }

    data class CloneFailed(
        val url: String,
        val path: Path = configuration.global.localStatePath,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_CLONE_FAILED"
        override val message: String
            get() = "Failed to git clone $url to ${path.pathString}"
    }

    data class AddFailed(
        val repoPath: Path,
        val targetPath: Path,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_ADD_FAILED"
        override val message: String
            get() = "Failed to git add ${targetPath.pathString} in repository at ${repoPath.pathString}"
    }

    data class CommitFailed(
        val path: Path,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_COMMIT_FAILED"
        override val message: String
            get() = "Failed to git commit in repository at ${path.pathString}"
    }

    data class PushFailed(
        val path: Path,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_PUSH_FAILED"
        override val message: String
            get() = "Failed to git push in repository at ${path.pathString}"
    }

    data class RemoteNotSet(
        val path: Path,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_REMOVE_NOT_SET"
        override val message: String
            get() = "Remote is not set in repository at ${path.pathString}"
    }


}
