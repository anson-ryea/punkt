package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.UnsyncOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int

object Unsync : PunktCommand() {
    val globalOptions by GlobalOptions()
    val handle by argument().int()

    override suspend fun run() {
        UnsyncOperation(
            globalOptions,
            handle,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            { echoSuccess(verbosityOption = globalOptions.verbosity) }
        )
    }
}