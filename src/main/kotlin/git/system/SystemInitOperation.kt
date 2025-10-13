package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.system.SystemGitUtils.isGitInstalled
import java.nio.file.Path

object SystemInitOperation {
    fun Raise<GitError>.systemInit(path: Path) {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        try {
            val process = ProcessBuilder("git", "init", path.toString()).start()

            val exitCode = process.waitFor()
            ensure(exitCode == 0) {
                GitError.InitFailed(path)
            }
        } catch (e: Exception) {
            raise(GitError.InitFailed(path, e))
        }
    }
}