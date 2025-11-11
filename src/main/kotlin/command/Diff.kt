package com.an5on.command

import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.DiffOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * Display differences between active and local states.
 *
 * @property commonOptions the common options for recursive, include, and exclude
 * @property paths the list of paths to diff, or null to diff all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Diff : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val paths by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique().optional()

    override fun run() {
        DiffOperation(
            paths,
            globalOptions,
            commonOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )
    }
}