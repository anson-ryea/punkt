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
import org.eclipse.jgit.lib.Constants
import org.eclipse.jgit.lib.SubmoduleConfig
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

/**
 * A Git operation to fetch from and integrate with another repository or a local branch.
 *
 * This class implements the `git pull` command, which is a shorthand for `git fetch` followed by `git merge` or
 * `git rebase`. It can operate using either the bundled JGit library or the system's native Git executable.
 * The operation can be configured to automatically stash local changes, rebase instead of merge, and handle submodules.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path to the Git repository. Defaults to the `localStatePath` from the
 * application's configuration.
 * @param autoStash If `true`, automatically creates a temporary stash entry before the operation begins, and applies it after.
 * @param rebase If `true`, rebases the current branch on top of the upstream branch after fetching.
 * @param recurseSubmodules If `true`, updates all active submodules to their corresponding commits in the superproject.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class PullOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val autoStash: Boolean = false,
    private val rebase: Boolean = false,
    private val recurseSubmodules: Boolean = true
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    /**
     * Performs a `git pull` using the bundled JGit library.
     *
     * This implementation handles the following logic:
     * 1.  Checks if a remote repository is configured.
     * 2.  If `autoStash` is enabled and there are local changes, it creates a stash.
     * 3.  Executes the pull command, configuring it with rebase, submodule, and credential settings.
     * 4.  In a `finally` block, it ensures that any created stash is applied and dropped, even if the pull fails.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit>  = either {
        val localRepository = Git.open(repositoryPath.toFile())

        val pullCommand = localRepository.pull()

        ensure(pullCommand.remote != null) {
            GitError.RemoteNotSet(repositoryPath)
        }

        val status = localRepository.status().call()
        val hasHead = localRepository.repository.resolve(Constants.HEAD) != null
        var isStashed = false

        if (autoStash && !status.isClean && hasHead) {
            val rev = localRepository.stashCreate()
                .setIncludeUntracked(true)
                .call()
            isStashed = rev != null
        }

        try {
            pullCommand.apply {
                setRebase(rebase)
                setRecurseSubmodules(
                    when {
                        recurseSubmodules -> SubmoduleConfig.FetchRecurseSubmodulesMode.ON_DEMAND
                        else -> SubmoduleConfig.FetchRecurseSubmodulesMode.NO
                    }
                )

                setTransportConfigCallback { transport ->
                    if (transport is SshTransport) {
                        transport.sshSessionFactory = sshSessionFactory
                    }
                }
                setCredentialsProvider(buildCredentialsProvider().bind())
            }.call()
        } catch (e: GitAPIException) {
            raise(GitError.BundledGitOperationFailed("Pull", e))
        } finally {
            if (isStashed) {
                try {
                    localRepository.stashApply().call()
                    localRepository.stashDrop().setStashRef(0).call()
                } catch (e: GitAPIException) {
                    raise(GitError.BundledGitOperationFailed("Pull", e))
                }
            }
        }
    }

    /**
     * Performs a `git pull` using the system's native `git` command.
     *
     * This implementation builds and executes a `git pull` command with the appropriate command-line arguments
     * for rebasing, auto-stashing, and handling submodules based on the properties of the class.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val hasRemote = executeSystemGitToCodeAndString(listOf("remote"), repositoryPath).bind().second
            .lines()
            .any { it.isNotBlank() }

        ensure(hasRemote) {
            GitError.RemoteNotSet(repositoryPath)
        }

        val args = mutableListOf("pull").apply {
            rebase.takeIf { it }?.let { add("--rebase") }
            autoStash.takeIf { it }?.let { add("--autostash") }
            recurseSubmodules.takeIf { it }?.let { add("--recurse-submodules=on-demand") }
        }

        executeSystemGit(args, repositoryPath).bind()
    }
}