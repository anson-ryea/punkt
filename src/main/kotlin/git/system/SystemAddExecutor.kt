package com.an5on.git.system

import arrow.core.raise.Raise
import com.an5on.error.GitError
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemAddExecutor : SystemGitExecutor() {
    fun Raise<GitError>.systemAdd(repoPath: Path, targetPath: Path): Int {
        val relativeTargetPath = repoPath.relativize(targetPath)
        val args = listOf("add", ".${relativeTargetPath.pathString}")

        return execute(args, repoPath)
    }
}