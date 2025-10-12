package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.DiffOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.DiffOperation.diff
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Display differences between active and local states.
 *
 * @property commonOptionGroup the common options for recursive, include, and exclude
 * @property paths the list of paths to diff, or null to diff all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Diff : CliktCommand() {
    private val commonOptionGroup by CommonOptionGroup()
    private val paths by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique().optional()

    override fun run() {
        val options = DiffOptions(
            commonOptionGroup.recursive,
            commonOptionGroup.include,
            commonOptionGroup.exclude
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)


        fold(
            { diff(paths, options, echos) },
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