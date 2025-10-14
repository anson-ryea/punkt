package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledGitCredentialsProvider.buildCredentialsProvider
import com.an5on.git.bundled.BundledGitCredentialsProvider.sshSessionFactory
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

object BundledCloneOperation {
    fun Raise<GitError>.bundledClone(
        path: Path,
        repoUrl: String,
        ssh: Boolean = false,
        branch: String? = null,
        depth: Int? = null
    ) {
        catch({
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
            }
                .call()
        }, { e ->
            when (e) {
                is InvalidRemoteException -> GitError.InvalidRemote(repoUrl, e)
                else -> throw e
            }
        })
    }
}