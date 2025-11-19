package com.an5on

import com.an5on.command.*
import com.an5on.command.List
import com.an5on.hub.command.*
import com.an5on.system.SystemUtils.logPath
import com.github.ajalt.clikt.command.main
import com.github.ajalt.clikt.core.subcommands
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.io.path.pathString

/**
 * The main entry point for the `punkt` application.
 *
 * This function is responsible for:
 * 1.  Configuring the logging directory by setting the `log.dir` system property.
 * 2.  Establishing a global uncaught exception handler to ensure all fatal errors are logged.
 * 3.  Constructing the command-line interface by initialising a root `Command` and attaching all the available
 *     subcommands (e.g., `Init`, `Sync`, `Activate`).
 * 4.  Parsing the command-line arguments and dispatching execution to the appropriate subcommand.
 *
 * @param args The command-line arguments passed to the application.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
suspend fun main(args: Array<String>) {
    System.setProperty("log.dir", logPath.pathString)

    val logger = KotlinLogging.logger {}

    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        val message = "Exception in thread ${t.name}: ${e.message}\n" +
                e.stackTraceToString()

        System.err.println(message)
        logger.error { message }
    }

    Base.subcommands(
        Init,
        Sync,
        Unsync,
        Activate,
        List,
        Diff,
        Git,
        Shell,
        Update,
        Ignored,
        LocalPath,
        ActivePath,
        Hub.subcommands(
            Register,
            Login,
            Logout,
            WhoAmI,
            ActivateLicence
        )
    ).main(args)
}