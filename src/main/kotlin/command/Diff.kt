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
 * A command to display the differences between files in the active state and their corresponding versions in the
 * local repository.
 *
 * This command compares files and directories in the user's filesystem (active state) with the versions stored in
 * the `punkt` local state. It highlights additions, deletions, and modifications, providing a clear overview of
 * changes. If no specific paths are provided, it will diff all tracked files.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 * @property commonOptions The common options for the command, such as recursion and filtering.
 * @property paths The list of specific file or directory paths to diff. If empty, all tracked files are diffed.
 */
object Diff : PunktCommand() {
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
            {}
        )
    }
}