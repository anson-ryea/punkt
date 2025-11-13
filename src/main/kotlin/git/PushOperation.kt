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

class PushOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    private val force: Boolean = false
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
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

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = mutableListOf("push").apply {
            force.takeIf { it }?.let { add("--force") }
            add("--atomic")
        }

        executeSystemGit(args, repositoryPath).bind()
    }
}