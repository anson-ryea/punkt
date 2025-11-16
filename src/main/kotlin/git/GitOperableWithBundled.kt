package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration
import com.an5on.error.GitError
import com.an5on.error.PunktError
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS

/**
 * An interface for Git operations that are implemented using the bundled JGit library.
 *
 * This interface extends [GitOperable] and is intended for commands that need to interact with a Git repository
 * programmatically without relying on a system-installed Git executable. It provides a framework for building
 * credentials and handling different authentication methods for both HTTPS and SSH protocols.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface GitOperableWithBundled : GitOperable {
    override fun operate(): Either<PunktError, Unit> =
        operateWithBundled()

    /**
     * The core logic of the Git operation, implemented using the bundled JGit library.
     *
     * Concrete classes must implement this method to define the specific JGit-based action (e.g., clone, pull).
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    fun operateWithBundled(): Either<GitError, Unit>

    /**
     * A companion object containing utility functions for building credentials for JGit operations.
     */
    companion object {
        /**
         * Builds a [CredentialsProvider] for HTTPS authentication by trying a series of strategies.
         *
         * This function iterates through the credential-providing methods defined in the application's configuration
         * (`git.builtInCredentialsPreference`). It attempts each method in order and returns the first successfully
         * created [CredentialsProvider].
         *
         * The supported strategies include:
         * - [GitCredentialsProviderForBundledType.GCM]: Git Credential Manager.
         * - [GitCredentialsProviderForBundledType.ENV]: Environment variables (`GIT_USERNAME`, `GIT_PASSWORD`).
         * - [GitCredentialsProviderForBundledType.GH_CLI]: GitHub CLI (`gh auth token`).
         *
         * @return An [Either] containing the first available [CredentialsProvider] or a [GitError.BundledCredentialsNotFound]
         * if no credentials could be obtained.
         */
        fun buildCredentialsProvider(): Either<GitError, CredentialsProvider> = either {
            for (method in ActiveConfiguration.configuration.git.builtInCredentialsPreference) {
                val provider = when (method) {
                    GitCredentialsProviderForBundledType.GCM -> {
                        buildCredentialsProviderFromGitCredentialManager().getOrNull()
                    }

                    GitCredentialsProviderForBundledType.ENV -> {
                        buildCredentialsProviderFromEnvironment()
                    }

                    GitCredentialsProviderForBundledType.GH_CLI -> {
                        buildCredentialsProviderFromGhCli().getOrNull()
                    }
                }

                if (provider != null) {
                    return@either provider
                }
            }

            raise(GitError.BundledCredentialsNotFound(ActiveConfiguration.configuration.git.builtInCredentialsPreference))
        }

        /**
         * Attempts to build a [CredentialsProvider] by querying the Git Credential Manager (GCM).
         *
         * This function executes `git-credential-manager get` to retrieve stored credentials for `github.com` over HTTPS.
         *
         * @return A [Result] containing a [UsernamePasswordCredentialsProvider] on success, or an exception on failure.
         * Returns `null` within the [Result] if GCM does not provide a username or password.
         */
        private fun buildCredentialsProviderFromGitCredentialManager(): Result<UsernamePasswordCredentialsProvider?> =
            runCatching {
                // ask GCM for host=github.com over HTTPS
                val process = ProcessBuilder("git-credential-manager", "get")
                    .start().apply {
                        outputStream.use {
                            it.write("protocol=https\nhost=github.com\n\n".toByteArray())
                            it.flush()
                        }
                    }

                // output from GCM is in the format:
                // username=audrey
                // password=hello
                val map = process.inputStream.bufferedReader().lineSequence()
                    .mapNotNull { it.split('=', limit = 2).takeIf { it.size == 2 }?.let { it[0] to it[1] } }
                    .toMap()

                val username = map["username"]
                val password = map["password"]

                if (username == null || password == null)
                    null
                else
                    UsernamePasswordCredentialsProvider(username, password)

            }

        /**
         * Builds a [CredentialsProvider] from `GIT_USERNAME` and `GIT_PASSWORD` environment variables.
         *
         * @return A [UsernamePasswordCredentialsProvider] if both environment variables are set, otherwise `null`.
         */
        private fun buildCredentialsProviderFromEnvironment(): UsernamePasswordCredentialsProvider? {
            val username = System.getenv("GIT_USERNAME")
            val password = System.getenv("GIT_PASSWORD")

            return if (username == null || password == null) {
                null
            } else {
                UsernamePasswordCredentialsProvider(username, password)
            }
        }

        /**
         * Attempts to build a [CredentialsProvider] by getting an authentication token from the GitHub CLI.
         *
         * This function executes `gh auth token` to retrieve a token, which is then used as the username for
         * authentication (with an empty password).
         *
         * @return A [Result] containing a [UsernamePasswordCredentialsProvider] on success, or an exception on failure.
         * Returns `null` within the [Result] if the token is blank.
         */
        private fun buildCredentialsProviderFromGhCli(): Result<UsernamePasswordCredentialsProvider?> = runCatching {
            val process = ProcessBuilder("gh", "auth", "token").start()
            val token = process.inputStream.bufferedReader().readText().trim()

            if (token.isBlank())
                null
            else
                UsernamePasswordCredentialsProvider(token, "")
        }

        /**
         * A custom JGit session factory for configuring SSH connections.
         *
         * This factory is used to set SSH client-side options, such as disabling strict host key checking.
         * The `createDefaultJSch` method is currently a placeholder and needs to be implemented to load
         * known hosts and private keys for proper SSH authentication.
         */
        val sshSessionFactory = object : JschConfigSessionFactory() {
            override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
                session?.setConfig("StrictHostKeyChecking", "ask")
            }

            override fun createDefaultJSch(fs: FS?): JSch? {
                val jsch = super.createDefaultJSch(fs)
                TODO()
                return jsch
            }
        }

        /**
         * A list of common filenames for SSH private keys.
         *
         * This list can be used to search for default identity files when establishing an SSH connection.
         */
        private val commonSshEncryptionAlgorithms = listOf(
            "id_rsa",
            "id_ed25519",
            "id_dsa",
            "id_ecdsa"
        )

        /**
         * A placeholder function intended to find an SSH identity file for a given host.
         *
         * This function needs to be implemented to locate the appropriate private key file (e.g., from `~/.ssh/`)
         * to be used for authenticating with a specific remote host.
         *
         * @param host The hostname to find an SSH identity for.
         */
        private fun findIdentity(host: String) {
            TODO()
        }
    }
}