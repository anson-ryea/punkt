package com.an5on.command

import com.an5on.command.List.commonOptions
import com.an5on.command.List.globalOptions
import com.an5on.command.List.paths
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.operation.ListOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.arguments.*
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import com.github.ajalt.clikt.parameters.types.path

/**
 * A command to list files and directories managed within the `punkt` local repository.
 *
 * This command provides a view of the files stored in the local state, which serves as the source for activated
 * dotfiles. It can list all tracked files or a specific subset based on the provided paths and filtering options.
 * This is useful for inspecting the contents of the `punkt` repository without navigating the filesystem directly.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 * @property commonOptions The common options for the command, such as filtering by inclusion/exclusion patterns.
 * @property paths The specific file or directory paths to list. If empty, all files in the local state are listed.
 */
object List : PunktCommand() {
    init {
        installMordantMarkdown()
    }
    private val globalOptions by GlobalOptions()
    private val commonOptions by CommonOptions()
    private val paths by argument().convert {
        it.expandTildeWithHomePathname()
    }.path(
        canBeFile = true,
        canBeDir = true,
        canBeSymlink = true
    ).multiple().unique().optional()

    override fun help(context: Context): String = """
        List files and directories managed within Punkt's local state.
        
        Examples:
        ```
        punkt list
        punkt list ~/.txt ~/audrey
        punkt list -i ".*.txt"
        punkt list --path-style local-relative -i ".*.txt" -x ".*/a.txt" /users/audrey
        ```
    """.trimIndent()

    override suspend fun run() {
        ListOperation(
            paths,
            globalOptions,
            commonOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {}
        )
    }
}