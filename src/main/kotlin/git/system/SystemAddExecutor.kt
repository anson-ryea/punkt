package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.GitUtils.isGitInstalled
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemAddExecutor: SystemGitExecutor() {
    fun Raise<GitError>.systemAdd(repoPath: Path, targetPath: Path): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        val relativeTargetPath = repoPath.relativize(targetPath)
        val args = listOf("add", ".${relativeTargetPath.pathString}")

        return execute(args, repoPath)
    }
}