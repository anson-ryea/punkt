package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledGitCredentialsProvider.buildCredentialsProvider
import com.an5on.git.bundled.BundledGitCredentialsProvider.sshSessionFactory
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

object BundledCloneOperation {
    fun Raise<GitError>.bundledClone(
        path: Path,
        repoUrl: String,
        ssh: Boolean = false,
        branch: String? = null,
        depth: Int? = null
    ) = catch({
        Git.cloneRepository().apply {
            setDirectory(path.toFile())
            setURI(repoUrl)

            branch?.let { setBranch(it) }
            depth?.let { setDepth(it) }

            if (ssh) {
                setTransportConfigCallback { transport ->
                    if (transport is SshTransport) {
                        transport.sshSessionFactory = sshSessionFactory
                    }
                }
            } else {
                setCredentialsProvider(buildCredentialsProvider())
            }
        }.call()
    }) {
        when (it) {
            is GitAPIException -> raise(GitError.BundledGitOperationFailed("Clone", it))
            else -> throw it
        }
    }
}