package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import com.an5on.git.GitUtils
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.nio.file.Path

object BundledCommitOperation {
    fun Raise<GitError>.bundledCommit(path: Path, message: String): Unit =
        catch(
            {
                val localRepo = Git.open(path.toFile())

                localRepo.commit()
                    .setAuthor(GitUtils.bundledIdentity)
                    .setMessage(message)
                    .call()
            })
        {
            when (it) {
                is GitAPIException -> raise(GitError.BundledGitOperationFailed("Add", it))
                else -> throw it
            }
        }
}