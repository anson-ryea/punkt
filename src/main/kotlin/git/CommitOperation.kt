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

class CommitOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val message: String
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val bundledIdentity = PersonIdent(configuration.git.bundledGitName, configuration.git.bundledGitEmail)

    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        val localRepository = Git.open(repositoryPath.toFile())

        localRepository.commit()
            .setAuthor(bundledIdentity)
            .setMessage(message)
            .call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Commit", it)
    }

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("commit", "-m", message)

        executeSystemGit(args, repositoryPath).bind()
    }

    companion object {
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