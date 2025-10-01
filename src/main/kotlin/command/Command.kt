package com.an5on.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

/**
 * The base command for Punkt.
 */
class Command: CliktCommand(name="punkt") {
    init {
        versionOption("")
    }

    override fun run() = Unit
}