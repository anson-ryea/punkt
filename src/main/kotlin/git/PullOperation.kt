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
import org.eclipse.jgit.lib.SubmoduleConfig
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

class PullOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val autoStash: Boolean = false,
    private val rebase: Boolean = false,
    private val recurseSubmodules: Boolean = true
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    override fun operateWithBundled(): Either<GitError, Unit>  = either {
        val localRepository = Git.open(repositoryPath.toFile())

        val pullCommand = localRepository.pull()

        ensure(pullCommand.remote != null) {
            GitError.RemoteNotSet(repositoryPath)
        }

        val status = localRepository.status().call()
        var isStashed = false

        if (autoStash && !status.isClean) {
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
                localRepository.stashApply().call()
                localRepository.stashDrop().setStashRef(0).call()
            }
        }
    }

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = mutableListOf("pull").apply {
            rebase.takeIf { it }?.let { add("--rebase") }
            autoStash.takeIf { it }?.let { add("--autostash") }
            recurseSubmodules.takeIf { it }?.let { add("--recurse-submodules=on-demand") }
        }

        executeSystemGit(args).bind()
    }
}