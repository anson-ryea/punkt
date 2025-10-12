package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.ListOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.ListOperation.list
import com.an5on.operation.PathStyles
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

class List : CliktCommand() {
    private val commonOptionGroup by CommonOptionGroup()
    private val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying the list of managed dotfiles. Options are 'absolute' or 'relative' to the home directory."
    ).choice(
        *PathStyles.entries
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray(),
    ).default("absolute")
    val paths by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        val options = ListOptions(
            commonOptionGroup.include,
            commonOptionGroup.exclude,
            PathStyles.valueOf(pathStyle.uppercase().replace("-", "_")),
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        fold(
            { list(paths, options, echos) },
            { e ->
                logger.error { e.message }
                throw ProgramResult(e.statusCode)
            },
            {
                echoSuccess()
            }
        )
    }

    private val logger = KotlinLogging.logger {}
}