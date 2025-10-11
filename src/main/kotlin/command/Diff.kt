package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.DiffOptions
import com.an5on.file.FileUtils.replaceTildeWithAbsPathname
import com.an5on.operation.DiffOperation.diff
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

class Diff : CliktCommand() {
    val recursive by option("-r", "--recursive", help = "Diff directories recursively").flag(default = true)
    val include by option("-i", "--include", help = "Include paths matching the regex pattern")
    val exclude by option("-x", "--exclude", help = "Exclude paths matching the regex pattern")
    val paths by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).multiple().unique().optional()

    override fun run() {
        val options = DiffOptions(
            recursive,
            include?.toRegex() ?: Regex(".*"), // Matches everything if include is null
            exclude?.toRegex() ?: Regex("$^") // Matches nothing if exclude is null
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)


        fold(
            { diff(paths, options, echos) },
            { e ->
                echo(e.message, err = true)
                exitProcess(e.statusCode)
            },
            {
                echoSuccess()
            }
        )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}