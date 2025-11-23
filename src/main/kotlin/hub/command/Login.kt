package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.LoginOptions
import com.an5on.hub.operation.LoginOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that logs a user into Punkt Hub.
 *
 * This invokes the remote authentication endpoint using the
 * credentials and settings provided via [loginOptions].
 *
 * On success, an access token is stored according to the active configuration.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Login : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val loginOptions by LoginOptions()

    override suspend fun run() {
        LoginOperation(
            globalOptions,
            loginOptions,
            echos,
            terminal
        ).run().fold(
            {
                handleError(it)
            },
            {
                echoSuccess(
                    "Welcome back, ${loginOptions.email}!",
                    verbosityOption = globalOptions.verbosity
                )
            }
        )
    }
}