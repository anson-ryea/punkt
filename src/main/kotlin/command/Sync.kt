package com.an5on.command

import com.an5on.operation.ManageState.sync
import com.an5on.operation.SyncOptions
import com.an5on.utils.FileUtils.replaceTildeWithAbsPath
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import io.github.oshai.kotlinlogging.KotlinLogging
import java.io.File

private val logger = KotlinLogging.logger {}

class Sync: CliktCommand() {
    val recursive by option("-r", "--recursive", help="Sync directories recursively").flag(default = true)
    val exclude by option("-x", "--exclude", help="Exclude files matching the regex pattern")
    val target by argument()

    override fun run() {
        val options = SyncOptions(
            recursive,
            exclude?.toRegex() ?: Regex("$^") // Matches nothing if exclude is null
        )

        val targetFile = File(replaceTildeWithAbsPath(target))

        sync(targetFile, options, ::echo)
    }
}