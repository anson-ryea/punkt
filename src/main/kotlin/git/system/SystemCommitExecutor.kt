package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.GitUtils.isGitInstalled
import java.nio.file.Path

object SystemCommitExecutor: SystemGitExecutor() {
    fun Raise<GitError>.systemCommit(path: Path, message: String): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        val args = listOf("commit", "-m", message)

        return execute(args, path)
    }
}