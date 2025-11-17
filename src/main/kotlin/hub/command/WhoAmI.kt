package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.GetSelfProfileOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

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