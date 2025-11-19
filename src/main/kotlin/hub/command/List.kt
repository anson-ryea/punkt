package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.ListOptions
import com.an5on.hub.operation.ListOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

object List : PunktCommand() {
    val globalOptions by GlobalOptions()
    val listOptions by ListOptions()

    override suspend fun run() {
        ListOperation(
            globalOptions,
            listOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {}
        )
    }
}