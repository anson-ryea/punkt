package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import arrow.core.raise.ensure
import com.an5on.error.GitError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.nio.file.Path

object BundledPushOperation {
    fun Raise<GitError>.bundledPush(path: Path, force: Boolean = false): Unit =
        catch(
            {
                val localRepo = Git.open(path.toFile())

                val pushCommand = localRepo.push()

                ensure(pushCommand.remote != null) {
                    GitError.RemoteNotSet(path)
                }

                pushCommand.setAtomic(true)
                    .setForce(force)
                    .call()
            })
        {
            when (it) {
                is GitAPIException -> raise(GitError.BundledGitOperationFailed("Add", it))
                else -> throw it
            }
        }

}