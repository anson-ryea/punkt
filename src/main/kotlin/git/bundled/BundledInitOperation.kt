package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.nio.file.Path

object BundledInitOperation {
    fun Raise<GitError>.bundledInit(path: Path): Unit =
        catch(
            {
                Git.init().setDirectory(path.toFile()).call()
            })
        {
            when (it) {
                is GitAPIException -> raise(GitError.BundledGitOperationFailed("Add", it))
                else -> throw it
            }
        }
}