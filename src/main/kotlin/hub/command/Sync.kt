package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.operation.SyncOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int

/**
 * Command that synchronises a collection on Punkt Hub to local state.
 *
 * The collection is identified by [handle], and the synchronisation
 * strategy is determined by [SyncOperation].
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Sync : PunktCommand() {
    val globalOptions by GlobalOptions()
    val handle by argument().int()

    override suspend fun run() {
        SyncOperation(
            globalOptions,
            handle,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            { echoSuccess(verbosityOption = Unsync.globalOptions.verbosity) }
        )
    }
}