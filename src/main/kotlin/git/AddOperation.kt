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

/**
 * A Git operation to add file contents to the index.
 *
 * This class implements the `git add` command for a specific target path. It can operate using either the bundled
 * JGit library or the system's native Git executable, based on the provided `useBundledGitOption`. The operation
 * stages changes for the specified `targetPath` within the given `repositoryPath`.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path to the Git repository. Defaults to the `localStatePath` from the
 * application's configuration.
 * @param targetPath The path of the file or directory to add to the Git index. The path is resolved relative to the
 * `repositoryPath`.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class AddOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    targetPath: Path
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val relativeTargetPath = targetPath.relativeTo(repositoryPath)
    private val pattern = relativeTargetPath.pathString.replace("\\", "/")

    /**
     * Adds the target path to the Git index using the bundled JGit library.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        val localRepository = Git.open(repositoryPath.toFile())

        localRepository.add()
            .addFilepattern(pattern)
            .call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Add", it)
    }

    /**
     * Adds the target path to the Git index using the system's native `git` command.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("add", pattern)

        executeSystemGit(args, repositoryPath).bind()
    }
}