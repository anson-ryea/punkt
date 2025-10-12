package com.an5on.git

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import com.an5on.git.GitUtils.buildCredentialsProvider
import com.an5on.git.GitUtils.parseRepoUrl
import com.an5on.git.GitUtils.sshSessionFactory
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.InvalidRemoteException
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path

object GitOperations {

    fun Raise<GitError>.initialiseRepository(path: Path) {
        catch(
            {
                Git.init().setDirectory(path.toFile()).call()
            },
            { e ->
                when (e) {
                    else -> throw e
                }
            }
        )
    }

    fun Raise<GitError>.cloneRepository(
        path: Path,
        repo: String,
        ssh: Boolean = false,
        branch: String? = null,
        depth: Int? = null
    ) {
        val repoUrl = parseRepoUrl(repo, ssh)

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
                    setCredentialsProvider(buildCredentialsProvider().bind())
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