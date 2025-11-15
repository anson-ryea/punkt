package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.toActive
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.types.path
import kotlin.io.path.pathString

object ActivePath : PunktCommand() {
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
                targets!!.joinToString(separator = "\n") { it.toActive().pathString }
            } else
                configuration.global.activeStatePath.pathString
        )
    }
}