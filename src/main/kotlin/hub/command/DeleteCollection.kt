package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.DeleteCollectionOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int

/**
 * Command that deletes an existing Punkt Hub collection.
 *
 * The target collection is identified by its integer handle.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object DeleteCollection : PunktCommand() {
    val globalOptions by GlobalOptions()
    val handle by argument().int()

    override suspend fun run() {
        DeleteCollectionOperation(
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