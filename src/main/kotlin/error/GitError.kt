package com.an5on.error

import com.an5on.git.GitCredentialsProviderForBundledType
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Represents errors related to Git and JGit operations.
 * These errors are typically recoverable and indicate a problem with the user's Git setup or a failed Git command.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
sealed interface GitError : PunktError {
    /**
     * An error indicating that no credentials could be found for the bundled Git client (JGit).
     * This occurs when the configured credential providers fail to supply the necessary authentication.
     *
     * @property methods The collection of credential provider types that were attempted.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class BundledCredentialsNotFound(
        val methods: Collection<GitCredentialsProviderForBundledType>,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_BUNDLED_CREDENTIALS_NOT_FOUND"
        override val message: String
            get() = "No credentials found for bundled Git using the configured methods: ${methods.joinToString(", ")}"
    }

    /**
     * An error indicating that the system's Git executable could not be found.
     * This usually means Git is not installed or not present in the system's PATH.
     *
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class SystemGitNotFound(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SYSTEM_GIT_NOT_FOUND"
        override val message: String
            get() = "System Git is not installed or not found in PATH,\n" +
                    "you may use bundled Git by --use-bundled-git option or setting 'git.useBundledGit' to true in configuration"
    }

    /**
     * An error indicating that a system Git operation has failed.
     *
     * @property args The arguments passed to the failed Git command.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class SystemGitOperationFailed(
        val args: List<String>,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_SYSTEM_OPERATION_FAILED"
        override val message: String
            get() = "System Git operation failed with arguments: ${args.joinToString(" ")}"
    }

    /**
     * An error indicating that a bundled Git (JGit) operation has failed.
     *
     * @property command The name of the command that failed.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class BundledGitOperationFailed(
        val command: String,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_BUNDLED_OPERATION_FAILED"
        override val message: String
            get() = "Bundled Git operation failed with command: $command"
    }

    /**
     * An error indicating that no remote repository is configured for a given local repository.
     *
     * @property path The path to the local repository.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class RemoteNotSet(
        val path: Path,
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "GIT_REMOVE_NOT_SET"
        override val message: String
            get() = "Remote is not set in repository at ${path.pathString}"
    }
}
