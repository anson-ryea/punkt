package com.an5on.command

import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.command.options.SyncOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.SyncOperation
import com.an5on.states.tracked.TrackedEntriesStore
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * A command to synchronise files from the active state (user's filesystem) to the local state (`punkt` repository).
 *
 * This command copies specified files and directories into the `punkt` local repository, making them available
 * for version control. It is the primary way to add or update files that `punkt` manages. The command can
 * operate on specific targets or, if none are provided, on all currently tracked files to ensure they are
 * up-to-date with the versions in the active state.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 * @property commonOptions The common options for the command, such as recursion and filtering.
 * @property syncOptions The options specific to the sync command, such as whether to remove files from the local
 * state that no longer exist in the active state.
 * @property targets The list of specific file or directory paths to synchronise. If empty, the command may sync all
 * tracked files.
 */
object Sync : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val syncOptions by SyncOptions()
    private val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique().optional()

    override fun run() {
        TrackedEntriesStore.connect()

        SyncOperation(
            targets,
            globalOptions,
            commonOptions,
            syncOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )

        TrackedEntriesStore.disconnect()
    }
}