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

/**
 * A Git operation to initialise an empty repository.
 *
 * This class implements the `git init` command. It can operate using either the bundled JGit library or the
 * system's native Git executable, based on the provided `useBundledGitOption`. The repository is created at the
 * specified `repositoryPath`.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path where the new Git repository will be created. Defaults to the `localStatePath`
 * from the application's configuration.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class InitOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    /**
     * Initialises a Git repository using the bundled JGit library.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit> = catchOrThrow<GitAPIException, Unit> {
        Git.init().setDirectory(repositoryPath.toFile()).call()
    }.mapLeft {
        GitError.BundledGitOperationFailed("Init", it)
    }

    /**
     * Initialises a Git repository using the system's native `git` command.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = listOf("init", repositoryPath.pathString)

        executeSystemGit(args).bind()
    }
}