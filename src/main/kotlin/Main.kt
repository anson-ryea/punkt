package com.an5on

import com.an5on.commands.Command
import com.an5on.commands.Init
import com.github.ajalt.clikt.core.main
import com.github.ajalt.clikt.core.subcommands

/**
 * Serves as the entry point for Punkt.
 * It initialises the [Command] class and adds subcommands to it.
 *
 * @param args Command-line arguments passed to Punkt.
 * @return [Unit]
 * @author Anson Ng
 */
fun main(args: Array<String>) = Command().subcommands(
    Init(),

).main(args)