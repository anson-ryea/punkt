package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.toLocal
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.types.path
import kotlin.io.path.pathString

/**
 * A command to display the absolute path in the local state corresponding to a given target path.
 *
 * If no targets are provided, it prints the path to the local state directory.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object LocalPath : PunktCommand() {
    /**
     * The list of target paths to resolve to their local state paths.
     * Tilde (`~`) is expanded to the user's home directory.
     */
    val targets by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()


    override fun help(context: Context): String = """
        Print the path to each target's local state when their active state path is given.
        If no targets are specified then print the path to the local state directory.
        
        Examples:
        punkt local-path
        punkt local-path ~/.audrey
    """.trimIndent()

    override fun run() {
        echo(
            if (targets != null && targets!!.isNotEmpty()) {
                targets!!.joinToString(separator = "\n") { it.toLocal().pathString }
            } else
                configuration.global.localStatePath.pathString
        )
    }
}