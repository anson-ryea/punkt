package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.LogoutOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that logs the current user out of Punkt Hub.
 *
 * This clears any stored authentication token so that subsequent
 * Hub operations require the user to log in again.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Logout : PunktCommand() {
    private val globalOptions by GlobalOptions()

    override suspend fun run() {
        LogoutOperation(
            globalOptions,
            echos,
            terminal
        ).run().fold(
            {
                handleError(it)
            },
            {
                echoSuccess(
                    "Successfully logged out.",
                    verbosityOption = globalOptions.verbosity
                )
            }
        )
    }
}