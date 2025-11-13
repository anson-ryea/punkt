package com.an5on.error

import com.an5on.git.CredentialsProviderForBundledType
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

    data class BundledCredentialsNotFound(
        val methods: Collection<CredentialsProviderForBundledType>,
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
            get() = "System Git is not installed or not found in PATH,\n" +
                    "you may use bundled Git by --use-bundled-git option or setting 'git.useBundledGit' to true in configuration"
    }

    data class SystemGitOperationFailed(
        val args: List<String>,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SYSTEM_OPERATION_FAILED"
        override val message: String
            get() = "System Git operation failed with arguments: ${args.joinToString(" ")}"
    }

    data class BundledGitOperationFailed(
        val command: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_BUNDLED_OPERATION_FAILED"
        override val message: String
            get() = "Bundled Git operation failed with command: $command"
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
