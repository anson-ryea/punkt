package com.an5on.command

import com.an5on.operation.StateOperations.sync
import com.an5on.operation.SyncOptions
import com.an5on.states.tracked.TrackedEntriesStore
import com.an5on.utils.FileUtils.replaceTildeWithAbsPathname
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.file
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class Sync: CliktCommand() {
    val recursive by option("-r", "--recursive", help="Sync directories recursively").flag(default = true)
    val include by option("-i", "--include", help="Include files matching the regex pattern")
    val exclude by option("-x", "--exclude", help="Exclude files matching the regex pattern")
    val targets by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.file(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).multiple().unique()

    override fun run() {
        val options = SyncOptions(
            recursive,
            include?.toRegex() ?: Regex(".*"), // Matches everything if include is null
            exclude?.toRegex() ?: Regex("$^") // Matches nothing if exclude is null
        )

        TrackedEntriesStore.connect()

        targets.forEach {
            sync(it, options, ::echo)
        }

        TrackedEntriesStore.disconnect()
    }
}