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
import kotlin.io.path.relativeTo

class AddOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    targetPath: Path
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val relativeTargetPath = targetPath.relativeTo(repositoryPath)

    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        val localRepository = Git.open(repositoryPath.toFile())

        localRepository.add()
            .addFilepattern(".${relativeTargetPath.pathString}")
            .call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Add", it)
    }

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("add", ".${relativeTargetPath.pathString}")

        executeSystemGit(args, repositoryPath).bind()
    }
}