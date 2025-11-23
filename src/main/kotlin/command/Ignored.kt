package com.an5on.command

import com.an5on.command.Ignored.globalOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.operation.IgnoredOperation
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.core.terminal
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * A command to list path matcher patterns that are ignored by Punkt so that they will not be synced to local state.
 *
 * This command identifies and displays files and directories that are currently being ignored based on
 * the repository's ignore rules (e.g., from a `.punktignore` file). It provides a quick way to see which
 * files are not being tracked.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity.
 */
object Ignored : PunktCommand() {
    init {
        installMordantMarkdown()
    }
    private val globalOptions by GlobalOptions()

    override fun help(context: Context): String = """
        List path matcher patterns that are ignored by Punkt so that they will not be synced to local state.
        
        Ignored rules are defined in the `.punktignore` file located at the top level of the local state directory.
        
        Examples:
        ```
        punkt ignored
        ```
    """.trimIndent()

    override suspend fun run() {
        IgnoredOperation(
            globalOptions,
            echos,
            terminal
        ).run().fold(
            { handleError(it) },
            {}
        )
    }
}