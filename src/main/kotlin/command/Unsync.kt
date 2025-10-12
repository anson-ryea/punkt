package com.an5on.command

import arrow.core.raise.fold
import com.an5on.file.FileUtils.replaceTildeWithHomeDirPathname
import com.an5on.operation.UnsyncOperation.unsync
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

class Unsync : CliktCommand() {
    val targets by argument().convert {
        replaceTildeWithHomeDirPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).convert { it.toRealPath() }.multiple().unique()

    override fun run() {
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        fold(
            { unsync(targets, echos) },
            { e ->
                logger.error { e.message }
                throw ProgramResult(e.statusCode)
            },
            {
                echoSuccess()
            }
        )
    }

    private val logger = KotlinLogging.logger {}
}