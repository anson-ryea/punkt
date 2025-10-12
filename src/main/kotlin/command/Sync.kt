package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.SyncOptions
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.SyncOperation.sync
import com.an5on.states.tracked.TrackedEntriesStore
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Synchronize files from the active state to the local state.
 *
 * @property commonOptionGroup the common options for recursive, include, and exclude
 * @property targets the list of target paths to sync, or null to sync all existing local files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Sync : CliktCommand() {
    private val commonOptionGroup by CommonOptionGroup()
    private val targets by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique().optional()

    override fun run() {
        val options = SyncOptions(
            commonOptionGroup.recursive,
            commonOptionGroup.include,
            commonOptionGroup.exclude
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        TrackedEntriesStore.connect()

        fold(
            { sync(targets, options, echos) },
            { e ->
                logger.error { "Error: " + e.message }
                throw ProgramResult(e.statusCode)
            },
            {
                echoSuccess()
            }
        )

        TrackedEntriesStore.disconnect()
    }

    private val logger = KotlinLogging.logger {}
}