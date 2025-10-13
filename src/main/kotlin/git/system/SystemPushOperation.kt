package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.system.SystemGitUtils.isGitInstalled
import java.nio.file.Path

object SystemPushOperation {
    fun Raise<GitError>.systemPush(path: Path, force: Boolean = false) {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        try {
            val args = mutableListOf("git", "push").apply{
                force.takeIf { it }?.let { add("--force") }
                add("--atomic")
            }

            val process = ProcessBuilder(*args.toTypedArray())
                .directory(path.toFile())
                .start()

            val exitCode = process.waitFor()
            ensure(exitCode == 0) {
                GitError.PushFailed(path)
            }
        } catch (e: Exception) {
            raise(GitError.PushFailed(path, e))
        }
    }
}