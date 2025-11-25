package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.RegisterOptions
import com.an5on.hub.operation.RegisterOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that registers a new Punkt Hub account.
 *
 * User details are taken from [registerOptions] and sent to the
 * registration endpoint. On success, the user can log in with the entered credentials.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Register : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val registerOptions by RegisterOptions()

    override fun help(context: Context): String = """
        Register a new Punkt Hub account.
        
        Example:
        ```
        punkt hub register
        ```
    """.trimIndent()

    override suspend fun run() {
        RegisterOperation(
            globalOptions,
            registerOptions,
            echos,
            terminal
        ).run().fold(
            {
                handleError(it)
            },
            {
                echoSuccess(
                    "Welcome to Punkt Hub! You can now log in with ${registerOptions.email}.",
                    globalOptions.verbosity
                )
            }
        )
    }
}