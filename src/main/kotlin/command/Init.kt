package com.an5on.command

import com.an5on.utils.GitUtils.buildCredentialsProvider
import com.an5on.utils.GitUtils.parseRepoUrl
import com.an5on.utils.GitUtils.remoteRepoPatterns
import com.an5on.utils.GitUtils.sshSessionFactory
import com.an5on.config.Configuration
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.core.installMordantMarkdown
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int
import io.github.oshai.kotlinlogging.KotlinLogging
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.transport.SshTransport
import java.io.File

private val logger = KotlinLogging.logger {}

/**
 * Initialises a Punkt local repository for storing the clones of the synced dotfiles.
 *
 * @property repo The URL of the remote Punkt repository to clone. If not provided, an empty local repository is initialised at [Configuration.localDirAbsPath].
 * Currently, [repo] supports the formats specified in [remoteRepoPatterns].
 * @property ssh A flag indicating whether to use SSH for cloning the remote Punkt repository.
 * @property branch The branch of the remote Punkt repository to clone. If not provided, the default branch is cloned.
 * @property depth The depth for a shallow clone of the remote Punkt repository. If not provided, a full clone is performed.
 * @see remoteRepoPatterns
 * @author Anson Ng
 */
class Init : CliktCommand() {
    init {
        installMordantMarkdown()
    }

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

override fun help(context: Context) = """
    Initialises a Punkt local repository at ${Configuration.localDirAbsPath}.
            
    If a remote Punkt repository URL is provided, it clones the repository to ${Configuration.localDirAbsPath}.
    Otherwise, it initialises an empty Punkt local repository at ${Configuration.localDirAbsPath}.
    
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

    override fun run() {
        if (checkLocalExists()) {
            echo("Punkt is already initialised at ${Configuration.localDirAbsPath}")
            return
        }

        if (repo == null) {
            try {
                val git = Git.init()
                    .setDirectory(File(Configuration.localDirAbsPath))
                    .call()
                echo("Initialised empty Punkt local repository at ${git.repository.directory}")
            } catch (e: Exception) {
                logger.error(e) { "Failed to initialise empty Punkt local repository at ${Configuration.localDirAbsPath}" }
                echo("Failed to initialise empty Punkt local repository at ${Configuration.localDirAbsPath}")
            }
        } else {
            val repoUrl = parseRepoUrl(repo!!, ssh == true)

            try {
                val git = Git.cloneRepository().apply {
                    setDirectory(File(Configuration.localDirAbsPath))
                    setURI(repoUrl)

                    if (branch != null) {
                        setBranch(branch)
                    }
                    if (depth != null) {
                        setDepth(depth!!)
                    }

                    if (ssh == true) {
                        setTransportConfigCallback { transport ->
                            if (transport is SshTransport) {
                                transport.sshSessionFactory = sshSessionFactory
                            }
                        }
                    } else {
                        setCredentialsProvider(buildCredentialsProvider())
                    }
                }
                    .call()
                echo("Cloned Punkt repository from $repoUrl to ${git.repository.directory}")
            } catch (e: Exception) {
                logger.error(e) { "Failed to clone Punkt repository from $repoUrl to ${Configuration.localDirAbsPath}" }
                echo("Failed to clone Punkt repository from $repoUrl to ${Configuration.localDirAbsPath}")
            }
        }
    }

    /** Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    private fun checkLocalExists() = File(Configuration.localDirAbsPath).exists()
}