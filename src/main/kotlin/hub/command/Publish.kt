package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.hub.operation.UploadFileToCollectionOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int
import com.github.ajalt.clikt.parameters.types.path

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