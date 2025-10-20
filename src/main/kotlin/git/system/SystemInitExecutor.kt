package com.an5on.git.system

import arrow.core.raise.Raise
import com.an5on.error.GitError
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemInitExecutor : SystemGitExecutor() {
    fun Raise<GitError>.systemInit(path: Path): Int {
        val args = listOf("init", path.pathString)

        return execute(args)
    }
}