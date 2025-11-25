package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.hub.operation.UploadFileToCollectionOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path

/**
 * Command that publishes one or more local files to a Punkt Hub collection.
 *
 * The files specified in [targets] are active state paths disk and uploaded the corresponding local state
 * to the collection identified by [handle].
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Publish : PunktCommand() {
    val globalOptions by GlobalOptions()
    val handle by argument().int()
    val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = false,
        canBeSymlink = false,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique()

    override fun help(context: Context): String = """
        Publish one or more local state files to a Punkt Hub collection.
        
        The targets should be in the form of active state paths.
        
        The collection is identified by its handle.
        
        Example:
        ```
        punkt hub publish 77 ./path/to/audrey.txt ./path/to/audrey2.kt
        ```
    """.trimIndent()

    override suspend fun run() {
        UploadFileToCollectionOperation(
            globalOptions,
            handle,
            targets,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            { echoSuccess(verbosityOption = globalOptions.verbosity) }
        )
    }
}