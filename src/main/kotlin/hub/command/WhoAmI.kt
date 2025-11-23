package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.GetSelfProfileOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that displays the profile of the currently authenticated Punkt Hub user.
 *
 * This queries the Hub API using the stored access token and prints a summary
 * of the active account, such as username and tier.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object WhoAmI : PunktCommand("whoami") {
    private val globalOptions by GlobalOptions()

    override suspend fun run() {
        GetSelfProfileOperation(
            globalOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {}
        )
    }
}