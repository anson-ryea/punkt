package com.an5on.command

import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.ActivateOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * A command to activate files, linking them from the local repository to their destination in the filesystem.
 *
 * This command takes a list of target files or directories and creates symbolic links to them from the `punkt`
 * local state to the user's filesystem, effectively "activating" them. If no targets are specified, it can be
 * configured to activate all tracked files.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 * @property commonOptions The common options for the command, such as recursion and filtering.
 * @property targets The list of target paths to activate. If empty, it may activate all tracked files based on configuration.
 */
object Activate : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        ActivateOperation(
            targets,
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