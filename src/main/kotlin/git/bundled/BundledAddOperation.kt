package com.an5on.git.bundled

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.error.GitError
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object BundledAddOperation {
    fun Raise<GitError>.bundledAdd(repoPath: Path, targetPath: Path): Unit =
        catch(
            {
                val relativeTargetPath = targetPath.relativeTo(repoPath)
                val localRepo = Git.open(repoPath.toFile())

                localRepo.add()
                    .addFilepattern(".${relativeTargetPath.pathString}")
                    .call()
            })
        {
            when (it) {
                is GitAPIException -> raise(GitError.BundledGitOperationFailed( "Add", it))
                else -> throw it
            }
        }
}