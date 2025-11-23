package com.an5on.hub.command

import com.github.ajalt.clikt.command.SuspendingCliktCommand

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
}