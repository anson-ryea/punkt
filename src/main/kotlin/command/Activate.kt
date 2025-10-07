package com.an5on.command

import com.an5on.operation.ActivateOptions
import com.an5on.operation.Operations.activate
import com.an5on.operation.Operations.activateExistingLocal
import com.an5on.utils.Echos
import com.an5on.utils.FileUtils.replaceTildeWithAbsPathname
import com.an5on.utils.echoStage
import com.an5on.utils.echoSuccess
import com.an5on.utils.echoWarning
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

class Activate: CliktCommand() {
    val recursive by option("-r", "--recursive", help="Sync directories recursively").flag(default = true)
    val include by option("-i", "--include", help="Include files matching the regex pattern")
    val exclude by option("-x", "--exclude", help="Exclude files matching the regex pattern")
    val targets by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        val options = ActivateOptions(
            recursive,
            include?.toRegex() ?: Regex(".*"), // Matches everything if include is null
            exclude?.toRegex() ?: Regex("$^") // Matches nothing if exclude is null
        )

        if (targets == null) {
            echoStage("Activating all dotfiles in Punkt local repository")
            activateExistingLocal(ActivateOptions(true, Regex(".*"), Regex("$^")),
                Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)
            ).fold(
                ifLeft = { e ->
                    echo(e.message, err = true)
                    logger.error { "${e.message}\n${e.cause?.stackTraceToString()}"}
                    exitProcess(e.statusCode)
                },
                ifRight = {
                    echoSuccess()
                }
            )
        } else {
            activate(targets!!, options, Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)).fold(
                ifLeft = { e ->
                    echo(e.message, err = true)
                    logger.error { "${e.message}\n${e.cause?.stackTraceToString()}"}
                    exitProcess(e.statusCode)
                },
                ifRight = {
                    echoSuccess()
                }
            )
        }
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}