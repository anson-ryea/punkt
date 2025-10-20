package com.an5on

import com.an5on.command.*
import com.an5on.command.List
import com.an5on.system.SystemUtils.logPath
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.path.pathString

/**
 * Serves as the entry point for Punkt.
 * It initialises the [Command] class and adds subcommands to it.
 *
 * @param args Command-line arguments passed to Punkt.
 * @return [Unit]
 * @author Anson Ng
 */
fun main(args: Array<String>) {
    System.setProperty("log.dir", logPath.pathString)

    val logger = KotlinLogging.logger {}

    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        val message = "Exception in thread ${t.name}: ${e.message}\n" +
                e.stackTraceToString()

        System.err.println(message)
        logger.error { message }
    }

    Command().subcommands(
        Init(),
        Sync(),
        Unsync(),
        Activate(),
        List(),
        Diff(),
        Git(),
        Shell()
    ).main(args)
}