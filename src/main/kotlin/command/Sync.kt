package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.SyncOptions
import com.an5on.file.FileUtils.replaceTildeWithAbsPathname
import com.an5on.operation.SyncOperation.sync
import com.an5on.states.tracked.TrackedEntriesStore
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlin.system.exitProcess

class Sync : CliktCommand() {
    val recursive by option("-r", "--recursive", help = "Sync directories recursively").flag(default = true)
    val include by option("-i", "--include", help = "Include files matching the regex pattern").convert { Regex(it) }
        .default(Regex(".*")) // Matches everything if include is null
    val exclude by option("-x", "--exclude", help = "Exclude files matching the regex pattern").convert { Regex(it) }
        .default(Regex("$^")) // Matches nothing if exclude is null
    val targets by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath().normalize() }.multiple().unique().optional()

    override fun run() {
        val options = SyncOptions(
            recursive,
            include,
            exclude
        )
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        TrackedEntriesStore.connect()

        fold(
            { sync(targets, options, echos) },
            { e ->
                echo(e.message, err = true)
                exitProcess(e.statusCode)
            },
            {
                echoSuccess()
            }
        )

        TrackedEntriesStore.disconnect()
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}