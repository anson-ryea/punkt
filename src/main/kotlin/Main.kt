package com.an5on

import com.an5on.command.Command
import com.an5on.command.Init
import com.an5on.command.Sync
import com.an5on.config.Configuration
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
fun main(args: Array<String>) {
    System.setProperty("log.dir", Configuration.defaultLogDirAbsPathname)

    Command().subcommands(
        Init(),
        Sync()
    ).main(args)
}