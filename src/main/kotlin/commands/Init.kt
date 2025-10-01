package com.an5on.commands

import com.an5on.commands.GitUtils.buildCredentialsProvider
import com.an5on.commands.GitUtils.remoteRepoPatterns
import com.an5on.commands.GitUtils.sshSessionFactory
import com.an5on.config.Configuration
import com.an5on.commands.GitUtils.parseRepoUrl
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.optional
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
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
 * @see remoteRepoPatterns
 * @author Anson Ng
 */
class Init: CliktCommand() {
    val repo: String? by argument().optional()
    val ssh: Boolean? by option().flag()

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
                        setURI(repoUrl)
                        setDirectory(File(Configuration.localDirAbsPath))
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