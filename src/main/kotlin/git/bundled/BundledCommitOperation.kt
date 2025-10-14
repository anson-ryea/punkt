package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import com.an5on.git.GitUtils
import org.eclipse.jgit.api.Git
import java.nio.file.Path

object BundledCommitOperation {
    fun Raise<GitError>.bundledCommit(path: Path, message: String) {
        catch(
            {
                val localRepo = Git.open(path.toFile())

                localRepo.commit()
                    .setAuthor(GitUtils.bundledIdentity)
                    .setMessage(message)
                    .call()
            },
            { e ->
                when (e) {
                    else -> throw e
                }
            }
        )
    }
}