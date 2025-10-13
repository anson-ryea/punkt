package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.system.SystemGitUtils.isGitInstalled
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemAddOperation {
    fun Raise<GitError>.systemAdd(repoPath: Path, targetPath: Path) {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        try {
            val args = mutableListOf("git", "add").apply{
                val relativeTargetPath = repoPath.relativize(targetPath)
                add(relativeTargetPath.pathString)
            }

            val process = ProcessBuilder(*args.toTypedArray())
                .directory(repoPath.toFile())
                .start()

            val exitCode = process.waitFor()
            ensure(exitCode == 0) {
                GitError.AddFailed(repoPath, targetPath)
            }
        } catch (e: Exception) {
            raise(GitError.AddFailed(repoPath, targetPath, e))
        }
    }
}