package com.an5on.git.system

import arrow.core.raise.Raise
import com.an5on.error.GitError
import java.nio.file.Path

object SystemPushExecutor : SystemGitExecutor() {
    fun Raise<GitError>.systemPush(path: Path, force: Boolean = false): Int {
        val args = mutableListOf("push").apply {
            force.takeIf { it }?.let { add("--force") }
            add("--atomic")
        }

        return execute(args, path)
    }
}