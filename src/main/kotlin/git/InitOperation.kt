package com.an5on.git

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.type.BooleanWithAuto
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.nio.file.Path
import kotlin.io.path.pathString

class InitOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        Git.init().setDirectory(repositoryPath.toFile()).call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Init", it)
    }

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("init", repositoryPath.pathString)

        executeSystemGit(args).bind()
    }
}