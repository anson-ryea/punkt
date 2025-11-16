package com.an5on.git

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.type.BooleanWithAuto
import org.apache.commons.text.StringSubstitutor
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.lib.PersonIdent
import java.nio.file.Path

/**
 * A Git operation to record changes to the repository.
 *
 * This class implements the `git commit` command. It can operate using either the bundled JGit library or the
 * system's native Git executable. The operation creates a new commit with a given `message` in the specified
 * `repositoryPath`.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path to the Git repository. Defaults to the `localStatePath` from the
 * application's configuration.
 * @param message The commit message.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class CommitOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val message: String
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val bundledIdentity = PersonIdent(configuration.git.bundledGitName, configuration.git.bundledGitEmail)

    /**
     * Creates a commit using the bundled JGit library.
     *
     * This implementation uses the configured author identity for the bundled Git and the provided commit message.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        val localRepository = Git.open(repositoryPath.toFile())

        localRepository.commit()
            .setAuthor(bundledIdentity)
            .setMessage(message)
            .call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Commit", it)
    }

    /**
     * Creates a commit using the system's native `git` command.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("commit", "-m", message)

        executeSystemGit(args, repositoryPath).bind()
    }

    /**
     * A companion object containing utility functions for commit messages.
     */
    companion object {
        /**
         * Substitutes variables in a commit message template.
         *
         * This function performs two levels of substitution:
         * 1.  It uses `StringSubstitutor.createInterpolator()` to replace standard lookups like `${sys:user.name}`.
         * 2.  It replaces a custom `${op}` variable with the provided `operationName`.
         *
         * This allows for dynamic and context-aware commit messages.
         *
         * @param commitMessage The commit message template containing variables.
         * @param operationName The name of the operation (e.g., "sync", "unsync") to substitute for the `${op}` variable.
         * @return The final commit message with all variables substituted.
         */
        fun substituteCommitMessage(commitMessage: String, operationName: String): String {
            val interpolator = StringSubstitutor.createInterpolator()
            val interpolatedMessage = interpolator.replace(commitMessage)
            val substitutor = StringSubstitutor(
                mapOf(
                    "op" to operationName
                )
            )
            val substitutedMessage = substitutor.replace(interpolatedMessage)
            return substitutedMessage
        }
    }
}