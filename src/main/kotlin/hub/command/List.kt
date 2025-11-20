package com.an5on.hub.command

import com.an5on.command.PunktCommand
import com.an5on.command.options.GlobalOptions
import com.an5on.hub.command.options.ListOptions
import com.an5on.hub.operation.GetCollectionByIdOperation
import com.an5on.hub.operation.ListCollectionsOperation
import com.an5on.hub.operation.ListSelfCollectionsOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.int

object List : PunktCommand() {
    val globalOptions by GlobalOptions()
    val listOptions by ListOptions()
    val handle by argument().int().optional()

    override suspend fun run() {
        // Must not group .fold calls as the operations are suspending
        if (listOptions.mine) {
            ListSelfCollectionsOperation(
                globalOptions,
                handle,
                echos,
                terminal
            ).run().fold(
                { handleError(it) },
                {}
            )
        } else if (handle == null) {
            ListCollectionsOperation(
                globalOptions,
                echos,
                terminal
            ).run().fold(
                { handleError(it) },
                {}
            )
        } else {
            GetCollectionByIdOperation(
                globalOptions,
                handle!!,
                echos,
                terminal
            ).run().fold(
                { handleError(it) },
                {}
            )
        }
    }
}