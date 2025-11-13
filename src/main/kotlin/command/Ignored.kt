package com.an5on.command

import com.an5on.command.options.GlobalOptions
import com.an5on.operation.IgnoredOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

object Ignored : PunktCommand() {
    private val globalOptions by GlobalOptions()
    override fun run() {
        IgnoredOperation(
            globalOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {}
        )
    }
}