package com.an5on.git

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.error.PunktError
import com.an5on.system.SystemUtils
import java.nio.file.Path

/**
 * An interface for Git operations that are implemented using the system's native Git executable.
 *
 * This interface extends [GitOperable] and provides utility methods for executing Git commands as external
 * processes. It handles process creation, I/O redirection, and error mapping, simplifying the implementation
 * of operations that delegate to `git` on the command line.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface GitOperableWithSystem : GitOperable {
    override fun operate(): Either<PunktError, Unit> =
        operateWithSystem().map { }

    /**
     * The core logic of the Git operation, implemented by calling the system's Git executable.
     *
     * Concrete classes must implement this method to define the specific Git command to be executed.
     *
     * @return An [Either] containing a [PunktError] on failure or the process's exit code on success.
     */
    fun operateWithSystem(): Either<PunktError, Int>

    /**
     * Executes a Git command as an external process, inheriting the standard input, output, and error streams.
     *
     * This is useful for interactive commands or when the output should be displayed directly to the user.
     *
     * @param args The list of arguments to pass to the Git command (e.g., `listOf("status", "-s")`).
     * @param workingPath The working directory in which to execute the command. Defaults to the current working directory.
     * @return An [Either] containing a [GitError] on failure (e.g., if `git` is not found) or the process's exit code on success.
     */
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

    /**
     * Executes a Git command as an external process and captures its output.
     *
     * This function redirects the process's standard error stream to its standard output stream and captures the
     * combined output as a string. It is useful for commands whose output needs to be parsed or processed.
     *
     * @param args The list of arguments to pass to the Git command.
     * @param workingPath The working directory in which to execute the command. Defaults to the current working directory.
     * @return An [Either] containing a [GitError] on failure or a [Pair] of the exit code and the captured output string on success.
     */
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