package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.ActivateLicenceOptions
import com.an5on.hub.operation.ActivateLicenceOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

object ActivateLicence : PunktCommand() {
    val globalOptions by GlobalOptions()
    val activateLicenceOptions by ActivateLicenceOptions()

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