package com.an5on.command

import com.an5on.git.GenericOperationWithSystem
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

/**
 * A command to execute arbitrary Git commands within the context of the local state repository.
 *
 * This command acts as a pass-through to the system's native Git executable, allowing users to run any Git command
 * directly on the underlying repository that `punkt` manages. This is useful for performing Git operations that are
 * not exposed through other `punkt` commands.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object Git : PunktCommand() {
    /**
     * The list of arguments to pass to the Git command.
     */
    private val arguments by argument().multiple()

    override fun help(context: Context): String = """
        Execute arbitrary Git commands within the context of the local state repository.
        
        This command only executes if Git is installed and available in the system's PATH.
        
        Examples:
        punkt git status
        punkt git log --oneline
        punkt git checkout -b new-branch
    """.trimIndent()

    override suspend fun run() {
        GenericOperationWithSystem(arguments)
            .operateWithSystem()
            .fold(
                { handleError(it) },
                {
                    throw ProgramResult(it)
                }
            )
    }
}