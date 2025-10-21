package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.UnsyncOperation.unsync
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * Unsynchronize files by removing them from the local state.
 *
 * @property targets the list of target paths to unsync
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Unsync : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val targets by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique()

    override fun run() {
        fold(
            { unsync(targets, globalOptions, echos, terminal) },
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )
    }
}