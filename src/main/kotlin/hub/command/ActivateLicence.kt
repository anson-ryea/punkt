package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.ActivateLicenceOptions
import com.an5on.hub.operation.ActivateLicenceOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that activates a Punkt Hub licence for the current user.
 *
 * This calls the remote activation endpoint and, on success,
 * enables unlimited downloads for the configured account.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object ActivateLicence : PunktCommand() {
    init {
        installMordantMarkdown()
    }

    val globalOptions by GlobalOptions()
    val activateLicenceOptions by ActivateLicenceOptions()

    override fun help(context: Context): String = """
        Activate a Punkt Hub licence to unlock unlimited downloads.
                
        Example usage:
        ```
        punkt hub activate-licence --licence-key XXXX-XXXX-XXXX-XXXX
        ```
    """.trimIndent()

    override suspend fun run() {
        ActivateLicenceOperation(
            globalOptions,
            activateLicenceOptions,
            echos,
            terminal
        ).run().fold({
            handleError(it)
        }, {
            echoSuccess(
                "You now have unlimited downloads on Punkt Hub. Thank you for activating your licence!",
                globalOptions.verbosity
            )
        })
    }
}