package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.ActivateOperation.activate
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * Activate files from the local state to the active state.
 *
 * @property commonOptions the common options for recursive, include, and exclude
 * @property targets the list of target paths to activate, or null to activate all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Activate : CliktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val targets by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        fold(
            { activate(targets, globalOptions, commonOptions, echos) },
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )
    }
}