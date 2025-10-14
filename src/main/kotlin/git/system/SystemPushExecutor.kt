package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.GitUtils.isGitInstalled
import java.nio.file.Path

object SystemPushExecutor: SystemGitExecutor() {
    fun Raise<GitError>.systemPush(path: Path, force: Boolean = false): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        val args = mutableListOf("git", "push").apply{
            force.takeIf { it }?.let { add("--force") }
            add("--atomic")
        }

        return execute(args, path)
    }
}