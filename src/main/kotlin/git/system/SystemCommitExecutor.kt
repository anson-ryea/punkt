package com.an5on.git.system

import arrow.core.raise.Raise
import com.an5on.error.GitError
import java.nio.file.Path

object SystemCommitExecutor : SystemGitExecutor() {
    fun Raise<GitError>.systemCommit(path: Path, message: String): Int {
        val args = listOf("commit", "-m", message)

        return execute(args, path)
    }
}