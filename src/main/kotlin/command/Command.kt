package com.an5on.command

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

/**
 * The base command for Punkt.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Command : CliktCommand(name = "punkt") {
    init {
        versionOption("")
    }

    override fun run() = Unit
}