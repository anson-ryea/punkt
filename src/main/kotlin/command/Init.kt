package com.an5on.command

import com.an5on.command.Init.globalOptions
import com.an5on.command.Init.initOptions
import com.an5on.command.Init.repo
import com.an5on.command.options.GlobalOptions
import com.an5on.command.options.InitOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.git.CloneOperation
import com.an5on.git.InitOperation
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * A command to initialise a `punkt` local repository for storing dotfiles.
 *
 * This command sets up the local repository, which is the foundation for managing your dotfiles with `punkt`.
 * It can either create a new, empty repository or clone an existing one from a remote source.
 *
 * ### Usage
 * - To create a new empty repository: `punkt init`
 * - To clone from a remote repository: `punkt init <repository_url>`
 *
 * The command will fail if a `punkt` repository has already been initialised.
 *
 * @property repo The optional URL of the remote repository to clone. If not provided, an empty local repository is created.
 * The command supports various URL formats and shortcuts.
 * @property globalOptions The global options for the command, such as verbosity.
 * @property initOptions The options specific to the init command, such as connection type (SSH) and clone depth.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object Init : PunktCommand() {
    init {
        installMordantMarkdown()
    }

    private val globalOptions by GlobalOptions()
    private val initOptions by InitOptions()
    private val repo: String? by argument(
        help = "Clone from the specified remote Punkt repository"
    ).optional()

    override fun help(context: Context) = """
    Initialises a Punkt local repository at ${configuration.global.localStatePath}.
            
    If a remote Punkt repository URL is provided, it clones the repository to ${configuration.global.localStatePath}.
    Otherwise, it initialises an empty Punkt local repository at ${configuration.global.localStatePath}.
    
    If the repository URL is not complete, Punkt will try to make guesses of it.
    Supported formats for the remote Punkt repository URL:

    Examples:
    ```
    punkt init
    punkt init audrey
    punkt init audrey/dotfiles
    punkt init --ssh audrey/config
    punkt init --branch main audrey/dotfiles
    punkt init --depth 1 audrey/dotfiles
    punkt init --ssh --branch main --depth 1 audrey/dotfiles
    ```
    """.trimIndent()

    /**
     * Executes the init command.
     *
     * Checks if Punkt is already initialized. If not, either initializes an empty local repository
     * or clones from the specified remote repository based on the provided arguments.
     */
    override suspend fun run() {
        if (LocalState.exists()) {
            handleError(LocalError.LocalAlreadyInitialised())
            return
        }

        if (repo.isNullOrBlank()) {
            InitOperation(
                globalOptions.useBundledGit
            ).run().fold(
                { handleError(it) },
                {
                    echoSuccess(verbosityOption = globalOptions.verbosity)
                }
            )
        } else {
            CloneOperation(
                globalOptions.useBundledGit,
                remoteRepository = repo!!,
                initOptions = initOptions,
            ).run().fold(
                { handleError(it) },
                {
                    echoSuccess(verbosityOption = globalOptions.verbosity)
                }
            )
        }
    }
}