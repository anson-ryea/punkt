package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.toLocal
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.convert
import com.github.ajalt.clikt.parameters.arguments.multiple
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.arguments.unique
import com.github.ajalt.clikt.parameters.types.path
import kotlin.io.path.pathString

object LocalPath : PunktCommand() {
    val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun run() {
        echo(
            if (targets != null && targets!!.isNotEmpty()) {
                targets!!.joinToString(separator = "\n") { it.toLocal().pathString }
            } else
                configuration.global.localStatePath.pathString
        )
    }
}