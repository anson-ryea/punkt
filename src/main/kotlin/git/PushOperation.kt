package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.GitOperableWithBundled.Companion.buildCredentialsProvider
import com.an5on.git.GitOperableWithBundled.Companion.sshSessionFactory
import com.an5on.type.BooleanWithAuto
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

/**
 * A Git operation to update remote refs along with associated objects.
 *
 * This class implements the `git push` command. It can operate using either the bundled JGit library or the
 * system's native Git executable. The operation can be configured to be atomic and to force-push.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path to the Git repository. Defaults to the `localStatePath` from the
 * application's configuration.
 * @param force If `true`, the push will be forced, overwriting the remote branch.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class PushOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val force: Boolean = false
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    /**
     * Performs a `git push` using the bundled JGit library.
     *
     * This implementation handles the following logic:
     * 1.  Checks if a remote repository is configured.
     * 2.  Executes the push command, configuring it to be atomic, to force-push if specified, and to use the
     *     appropriate credentials.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit> = either {
        val localRepository = Git.open(repositoryPath.toFile())

        val pushCommand = localRepository.push()

        ensure(pushCommand.remote != null) {
            GitError.RemoteNotSet(repositoryPath)
        }

        try {
            pushCommand.apply {
                setAtomic(true)
                setForce(force)

                setTransportConfigCallback { transport ->
                    if (transport is SshTransport) {
                        transport.sshSessionFactory = sshSessionFactory
                    }
                }
                setCredentialsProvider(buildCredentialsProvider().bind())
            }.call()
        } catch (e: GitAPIException) {
            raise(GitError.BundledGitOperationFailed("Push", e))
        }
    }

    /**
     * Performs a `git push` using the system's native `git` command.
     *
     * This implementation builds and executes a `git push` command with the appropriate command-line arguments
     * for forcing and atomicity based on the properties of the class.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = mutableListOf("push").apply {
            force.takeIf { it }?.let { add("--force") }
            add("--atomic")
        }

        executeSystemGit(args, repositoryPath).bind()
    }
}