package com.an5on.command

import arrow.core.raise.fold
import com.an5on.command.options.GlobalOptionGroup
import com.an5on.command.options.InitOptionGroup
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.git.CloneOperation.clone
import com.an5on.git.InitOperation.init
import com.an5on.git.RepoPattern.commonPatterns
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.provideDelegate
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Initialises a Punkt local repository for storing the clones of the synced dotfiles.
 *
 * @property repo The URL of the remote Punkt repository to clone. If not provided, an empty local repository at local state path.
 * Currently, [repo] supports the formats specified in [commonPatterns].
 * @property globalOptions The global options for the command, provided by [com.an5on.command.options.GlobalOptionGroup].
 * @property initOptions The options specific to the init command, provided by [InitOptionGroup].
 * @see commonPatterns
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Init : CliktCommand() {
    val globalOptions by GlobalOptionGroup()
    val initOptions by InitOptionGroup()
    val repo: String? by argument(
        help = "Clone from the specified remote Punkt repository"
    ).optional()

    override fun help(context: Context) = """
    Initialises a Punkt local repository at ${configuration.general.localStatePath}.
            
    If a remote Punkt repository URL is provided, it clones the repository to ${configuration.general.localStatePath}.
    Otherwise, it initialises an empty Punkt local repository at ${configuration.general.localStatePath}.
    
    If the repository URL is not complete, Punkt will try to make guesses of it.
    Supported formats for the remote Punkt repository URL:
    ${commonPatterns.joinToString("\n") { "- ${it.pattern}" }}
    
    Examples:
        punkt init
        punkt init audrey
        punkt init audrey/dotfiles
        punkt init --ssh audrey/config
        punkt init --branch main audrey/dotfiles
        punkt init --depth 1 audrey/dotfiles
        punkt init --ssh --branch main --depth 1 audrey/dotfiles
    """.trimIndent()

    /**
     * Executes the init command.
     *
     * Checks if Punkt is already initialized. If not, either initializes an empty local repository
     * or clones from the specified remote repository based on the provided arguments.
     */
    override fun run() {
        if (LocalState.exists()) {
            logger.error { "Punkt is already initialised at ${configuration.general.localStatePath}" }
            throw ProgramResult(1)
        }

        fold(
            {
            if (repo == null) {
                init(globalOptions.useBundledGit )
            } else {
                clone(repo!!, initOptions, globalOptions.useBundledGit)
            }
        },
            { e ->
                logger.error { e.message }
                throw ProgramResult(1)
            }, {
                echoSuccess()
            })
    }

    private val logger = KotlinLogging.logger {}
}