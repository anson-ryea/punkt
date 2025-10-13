package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.error.GitError
import com.an5on.git.system.SystemGitUtils.isGitInstalled
import java.nio.file.Path
import kotlin.io.path.pathString

object SystemCloneOperation {
    fun Raise<GitError>.systemClone(
        path: Path,
        repoUrl: String,
        branch: String? = null,
        depth: Int? = null
    ) {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        try {
            val args = mutableListOf("git", "clone").apply{
                branch?.let { add("--branch=$it") }
                depth?.let { add("--depth=$it") }
                add(repoUrl)
                add(path.pathString)
            }

            val process = ProcessBuilder(*args.toTypedArray()).start()

            val exitCode = process.waitFor()
            ensure(exitCode == 0) {
                GitError.CloneFailed(repoUrl, path)
            }
        } catch (e: Exception) {
            raise(GitError.CloneFailed(repoUrl, path, e))
        }
    }
}