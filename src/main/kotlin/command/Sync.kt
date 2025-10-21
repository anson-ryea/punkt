package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.SyncOperation.sync
import com.an5on.states.tracked.TrackedEntriesStore
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * Synchronize files from the active state to the local state.
 *
 * @property commonOptions the common options for recursive, include, and exclude
 * @property targets the list of target paths to sync, or null to sync all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Sync : PunktCommand() {
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
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

        fold(
            { sync(targets, globalOptions, commonOptions, echos, terminal) },
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )

        TrackedEntriesStore.disconnect()
    }
}