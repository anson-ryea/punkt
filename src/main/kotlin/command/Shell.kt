package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.SystemUtils
import com.an5on.system.SystemUtils.environmentVariables
import com.github.ajalt.clikt.core.ProgramResult
import com.pty4j.PtyProcessBuilder
import kotlin.io.path.pathString

/**
 * A command to launch an interactive shell session within the `punkt` local repository.
 *
 * This command opens a new pseudo-terminal (PTY) running the system's default shell (e.g., bash, PowerShell).
 * The shell's working directory is set to the `punkt` local state path, allowing the user to directly
 * interact with the files and the underlying Git repository managed by `punkt`.
 *
 * This is useful for running complex Git commands, manually editing files, or performing other tasks
 * that are not directly exposed through other `punkt` commands. The shell session inherits the environment
 * variables of the parent `punkt` process.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Shell : PunktCommand() {
    override suspend fun run() {
        val cmd = arrayOf(SystemUtils.shell)

        val builder = PtyProcessBuilder(cmd)
            .setDirectory(configuration.global.localStatePath.pathString)
            .setEnvironment(environmentVariables)
            .setConsole(true)
        val process = builder.start()

        // Forward PTY output to stdout
        Thread { process.inputStream.copyTo(System.out) }.apply {
            isDaemon = true
            start()
        }
        // Forward stdin to PTY input
        Thread { System.`in`.copyTo(process.outputStream) }.apply {
            isDaemon = true
            start()
        }

        val exitCode = process.waitFor()
        process.destroy()
        throw ProgramResult(exitCode)
    }
}