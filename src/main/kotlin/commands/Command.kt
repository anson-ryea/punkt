package com.an5on.commands

import com.github.ajalt.clikt.core.CliktCommand

/**
 * The base command for Punkt.
 */
class Command: CliktCommand(name="punkt") {
    override fun run() = Unit
}