package com.an5on.git

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.system.SystemUtils
import java.nio.file.Path

interface GitOperableWithSystem: GitOperable {
    override fun operate(): Either<GitError, Unit> =
        operateWithSystem().map { }

    fun operateWithSystem(): Either<GitError, Int>

    fun executeSystemGit(
        args: List<String>,
        workingPath: Path = SystemUtils.workingPath
    ): Either<GitError, Int> = catchOrThrow<Exception, Int> {
        val process = ProcessBuilder(configuration.git.systemGitCommand, *args.toTypedArray())
            .directory(workingPath.toFile())
            .inheritIO()
            .start()

        process.waitFor()
    }.mapLeft {
        when (it) {
            is java.io.IOException -> GitError.SystemGitNotFound()
            else -> GitError.SystemGitOperationFailed(args, it)
        }
    }

    fun executeSystemGitToCodeAndString(
        args: List<String>,
        workingPath: Path = SystemUtils.workingPath
    ): Either<GitError, Pair<Int, String>> = catchOrThrow<Exception, Pair<Int, String>> {
        val process = ProcessBuilder(configuration.git.systemGitCommand, *args.toTypedArray())
            .directory(workingPath.toFile())
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }
        val exitCode = process.waitFor()
        Pair(exitCode, output)
    }.mapLeft {
        when (it) {
            is java.io.IOException -> GitError.SystemGitNotFound()
            else -> GitError.SystemGitOperationFailed(args, it)
        }
    }
}