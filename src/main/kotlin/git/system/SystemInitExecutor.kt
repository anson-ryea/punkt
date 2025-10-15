package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.GitUtils.isGitInstalled
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemInitExecutor: SystemGitExecutor() {
    fun Raise<GitError>.systemInit(path: Path): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        val args = listOf("init", path.pathString)

        return execute(args)
    }
}