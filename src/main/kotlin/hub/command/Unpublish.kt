package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.DeleteFileFromCollectionOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int

/**
 * Command that removes a previously published file from a Punkt Hub collection.
 *
 * The file identified by [target], which is the filename on Punkt Hub, is deleted from the collection
 * referenced by [handle] on the remote Hub.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Unpublish : PunktCommand() {
    val globalOptions by GlobalOptions()
    val handle by argument().int()
    val target by argument()

    override suspend fun run() {
        DeleteFileFromCollectionOperation(
            globalOptions,
            handle,
            target,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            { echoSuccess(verbosityOption = globalOptions.verbosity) }
        )
    }
}