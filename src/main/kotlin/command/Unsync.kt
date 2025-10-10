package com.an5on.command

import com.an5on.operation.UnsyncOperation.unsync
import com.an5on.utils.FileUtils.replaceTildeWithAbsPathname
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.types.path
import io.github.oshai.kotlinlogging.KotlinLogging

class Unsync : CliktCommand() {
    val targets by argument().convert {
        replaceTildeWithAbsPathname(it)
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true,
        mustExist = true,
        mustBeReadable = true
    ).multiple().unique()

    override fun run() {
        val echos = Echos(::echo, ::echoStage, ::echoSuccess, ::echoWarning)

        unsync(targets, echos).fold(
            ifLeft = { e ->

            },
            ifRight = {
                echoSuccess()
            }
        )
    }

    companion object {
        private val logger = KotlinLogging.logger {}
    }
}