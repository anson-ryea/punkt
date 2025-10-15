package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.ListOperation.list
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * List files in the local state.
 *
 * @property commonOptions the common options for include and exclude
 * @property pathStyle the style for displaying paths
 * @property paths the list of paths to list, or null to list all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class List : CliktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val paths by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        fold(
            { list(paths, globalOptions, commonOptions, echos) },
            { e ->
                logger.error { e.message }
                throw ProgramResult(e.statusCode)
            },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )
    }

    private val logger = KotlinLogging.logger {}
}