package com.an5on.hub.command

import com.github.ajalt.clikt.command.SuspendingCliktCommand

/**
 * The base subcommand for Punkt Hub.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object Hub : SuspendingCliktCommand() {
    override suspend fun run() = Unit
}