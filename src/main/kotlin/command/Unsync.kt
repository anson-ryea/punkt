package com.an5on.command

import com.an5on.command.Unsync.globalOptions
import com.an5on.command.Unsync.targets
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.UnsyncOperation
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * A command to remove files and directories from the `punkt` local repository, effectively "unsyncing" them.
 *
 * This command deletes the specified targets from the local state, which means `punkt` will no longer track them.
 * This does not affect the original files in the active state (the user's filesystem). It is the counterpart
 * to the `sync` command and is used when you want to stop managing a file with `punkt`.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 * @property targets The list of target paths in the active state to remove from the local repository.
 */
object Unsync : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique()

    override suspend fun run() {
        UnsyncOperation(
            targets,
            globalOptions,
            CommonOptions(),
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