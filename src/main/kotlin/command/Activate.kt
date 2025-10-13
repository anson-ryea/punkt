package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.ActivateOptions
import com.an5on.command.options.CommonOptionGroup
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.ActivateOperation.activate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Activate files from the local state to the active state.
 *
 * @property commonOptionGroup the common options for recursive, include, and exclude
 * @property targets the list of target paths to activate, or null to activate all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
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