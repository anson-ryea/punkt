package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.catch
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.system.SystemUtils
import java.nio.file.Path

abstract class SystemGitExecutor {
    open fun Raise<GitError>.execute(args: List<String>, workingPath: Path = SystemUtils.workingPath): Int = catch({
        val process = ProcessBuilder(configuration.git.systemGitCommand, *args.toTypedArray())
            .directory(workingPath.toFile())
            .inheritIO()
            .start()

        return process.waitFor()
    }) {
        when (it) {
            is java.io.IOException -> raise(GitError.SystemGitNotFound())
            else -> raise(GitError.SystemGitOperationFailed(args, it))
        }
    }
}