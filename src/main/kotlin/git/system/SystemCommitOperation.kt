package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.system.SystemGitUtils.isGitInstalled
import java.nio.file.Path

object SystemCommitOperation {
    fun Raise<GitError>.systemCommit(path: Path, message: String) {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        try {
            val args = mutableListOf("git", "commit").apply{
                add("-m")
                add("\"$message\"")
            }

            val process = ProcessBuilder(*args.toTypedArray())
                .directory(path.toFile())
                .start()

            val exitCode = process.waitFor()
            ensure(exitCode == 0) {
                GitError.CommitFailed(path)
            }
        } catch (e: Exception) {
            raise(GitError.CommitFailed(path, e))
        }
    }
}