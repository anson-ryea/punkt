package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.CreateCollectionOptions
import com.an5on.hub.operation.CreateCollectionOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * Command that creates a new collection on Punkt Hub.
 *
 * The collection metadata, such as name and visibility, is
 * populated from [createCollectionOptions].
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object CreateCollection : PunktCommand() {
    val globalOptions by GlobalOptions()
    val createCollectionOptions by CreateCollectionOptions()

    override fun help(context: Context): String = """
        Create a new collection on Punkt Hub.
                
        Example usage:
        ```
        punkt hub create-collection --name "Audrey's Collection" --description "A collection of my favorite assets."
        punkt hub create-collection --name "Private Collection" --private
        ```
    """.trimIndent()

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