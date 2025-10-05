package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.GitError
import com.an5on.error.PunktError
import com.an5on.utils.GitUtils.buildCredentialsProvider
import com.an5on.utils.GitUtils.parseRepoUrl
import com.an5on.utils.GitUtils.sshSessionFactory
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.SshTransport
import java.io.File

object GitOperations {

    fun initialiseRepository(directory: File): Either<PunktError, Unit> = either {
        try {
            Git.init().setDirectory(directory).call()
        } catch (e: Exception) {
            when (e) {
                is GitAPIException -> GitError.InitFailed(directory.path, e)
                else -> throw e
            }
        }
    }

    fun cloneRepository(
        repo: String?,
        directory: File,
        ssh: Boolean = false,
        branch: String? = null,
        depth: Int? = null
    ): Either<PunktError, Unit> = either {

        try {
            val repoUrl = parseRepoUrl(repo!!, ssh)

            Git.cloneRepository().apply {
                setDirectory(directory)
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
        } catch (e: Exception) {
            when (e) {
                is GitAPIException -> GitError.CloneFailed(repo ?: "null", directory.path, e)
                else -> throw e
            }
        }
    }
}