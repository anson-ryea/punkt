package com.an5on.command

import arrow.core.raise.fold
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPathname
import com.an5on.config.Configuration
import com.an5on.git.GitOperations.cloneRepository
import com.an5on.git.GitOperations.initialiseRepository
import com.an5on.git.GitUtils.remoteRepoPatterns
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * Initialises a Punkt local repository for storing the clones of the synced dotfiles.
 *
 * @property repo The URL of the remote Punkt repository to clone. If not provided, an empty local repository is initialised at [Configuration.localDirAbsPathname].
 * Currently, [repo] supports the formats specified in [remoteRepoPatterns].
 * @property ssh A flag indicating whether to use SSH for cloning the remote Punkt repository.
 * @property branch The branch of the remote Punkt repository to clone. If not provided, the default branch is cloned.
 * @property depth The depth for a shallow clone of the remote Punkt repository. If not provided, a full clone is performed.
 * @see remoteRepoPatterns
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class Init : CliktCommand() {
    val repo: String? by argument(
        help = "Clone from the specified remote Punkt repository"
    ).optional()
    val ssh: Boolean? by option(
        help = "Use SSH for cloning"
    ).flag()
    val branch: String? by option(
        names = arrayOf("-b", "--branch"),
        help = "Set the branch of the remote Punkt repository to clone"
    )
    val depth: Int? by option(
        names = arrayOf("-p", "--depth"),
        help = "Clone the remote Punkt repository shallowly with the specified depth"
    ).int()

    /**
     * Provides the help message for the init command.
     *
     * @param context the context of the command
     * @return the help message string
     */
    override fun help(context: Context) = """
    Initialises a Punkt local repository at ${localDirAbsPathname}.
            
    If a remote Punkt repository URL is provided, it clones the repository to ${localDirAbsPathname}.
    Otherwise, it initialises an empty Punkt local repository at ${localDirAbsPathname}.
    
    If the repository URL is not complete, Punkt will try to make guesses of it.
    Supported formats for the remote Punkt repository URL:
    ${remoteRepoPatterns.joinToString("\n") { "- ${it.pattern}" }}
    
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
            logger.error { "Punkt is already initialised at $localDirAbsPathname" }
            throw ProgramResult(1)
        }

        if (repo == null) {
            fold(
                {
                    initialiseRepository(localDirAbsPath)
                },
                { e ->
                    logger.error { e.message }
                    throw ProgramResult(1)
                }, {
                    echoSuccess()
                }
            )
        } else {
            fold(
                {
                    cloneRepository(localDirAbsPath, repo!!, ssh ?: false, branch, depth)
                },
                { e ->
                    logger.error { e.message }
                    throw ProgramResult(1)
                }, {
                    echoSuccess()
                }
            )
        }
    }

    /**
     * Companion object for the Init command.
     */
    companion object {
        private val logger = KotlinLogging.logger {}
    }
}