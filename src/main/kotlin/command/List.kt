package com.an5on.command

import com.an5on.command.options.ListOptions
import com.an5on.operation.ListOperation.list
import com.an5on.operation.PathStyles
import com.an5on.utils.FileUtils.replaceTildeWithAbsPathname
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.path

class List : CliktCommand() {
    val include by option("-i", "--include", help = "Include paths matching the regex pattern")
    val exclude by option("-x", "--exclude", help = "Exclude paths matching the regex pattern")
    val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying the list of managed dotfiles. Options are 'absolute' or 'relative' to the home directory."
    ).choice(
        *PathStyles.entries
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray(),
    ).default("absolute")
    val paths by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        val options = ListOptions(
            include?.toRegex() ?: Regex(".*"), // Matches everything if include is null
            exclude?.toRegex() ?: Regex("$^"), // Matches nothing if exclude is null
            PathStyles.valueOf(pathStyle.uppercase().replace("-", "_")),
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        list(paths, options, echos)
    }
}