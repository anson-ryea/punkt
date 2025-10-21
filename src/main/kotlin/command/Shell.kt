package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.SystemUtils
import com.an5on.system.SystemUtils.environmentVariables
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.pty4j.PtyProcessBuilder
import kotlin.io.path.pathString

class Shell : CliktCommand() {
    override fun run() {
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