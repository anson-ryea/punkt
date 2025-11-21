package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.CreateCollectionOptions
import com.an5on.hub.operation.CreateCollectionOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

object CreateCollection : PunktCommand() {
    val globalOptions by GlobalOptions()
    val createCollectionOptions by CreateCollectionOptions()

    override suspend fun run() {
        CreateCollectionOperation(
            globalOptions,
            createCollectionOptions,
            echos,
            terminal
        ).run().fold(
            { err -> handleError(err) },
            { echoSuccess(verbosityOption = globalOptions.verbosity) }
        )
    }
}