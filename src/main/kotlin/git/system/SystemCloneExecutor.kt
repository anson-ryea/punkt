package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.GitUtils.isGitInstalled
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemCloneExecutor: SystemGitExecutor() {
    fun Raise<GitError>.systemClone(
        path: Path,
        repoUrl: String,
        branch: String? = null,
        depth: Int? = null
    ): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        val args = mutableListOf("clone").apply{
            branch?.let { add("--branch=$it") }
            depth?.let { add("--depth=$it") }
            add(repoUrl)
            add(path.pathString)
        }

        return execute(args)
    }
}