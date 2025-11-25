package com.an5on.hub.command

import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.Context

/**
 * The base subcommand for Punkt Hub.
 *
 * This groups all Hub-related subcommands under a single entry point.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Hub : SuspendingCliktCommand() {
    override suspend fun run() = Unit

    override fun help(context: Context): String = """
    Manage Punkt Hub collections and items.
    """.trimIndent()
}