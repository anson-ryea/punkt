package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.ActivateOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.ActivateOperation.activate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

class Activate : CliktCommand() {
    private val commonOptionGroup by CommonOptionGroup()
    private val targets by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        val options = ActivateOptions(
            commonOptionGroup.recursive,
            commonOptionGroup.include,
            commonOptionGroup.exclude
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        fold(
            { activate(targets, options, echos) },
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