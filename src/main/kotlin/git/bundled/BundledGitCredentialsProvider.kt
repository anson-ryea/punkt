package com.an5on.git.bundled

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration
import com.an5on.error.GitError
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS

/**
 * Utility object for Git-related operations.
 *
 * @property sshSessionFactory An SSH session factory that configures Jsch SSH sessions for remote Git operations.
 * @author Anson Ng
 */
object BundledGitCredentialsProvider {
    /**
     * Builds a [org.eclipse.jgit.transport.CredentialsProvider] using retrieve stored credentials for HTTPS authentication.
     * Currently, supports:
     * - Git Credential Manager (GCM)
     */
    fun Raise<GitError>.buildCredentialsProvider(): CredentialsProvider {
        for (method in ActiveConfiguration.configuration.git.builtInCredentialsPreference) {
            val provider = when (method) {
                BundledGitCredentialsProviderType.GCM -> {
                    buildCredentialsProviderFromGitCredentialManager().getOrNull()
                }

                BundledGitCredentialsProviderType.ENV -> {
                    buildCredentialsProviderFromEnvironment()
                }

                BundledGitCredentialsProviderType.GH_CLI -> {
                    buildCredentialsProviderFromGhCli().getOrNull()
                }
            }

            if (provider != null) {
                return provider
            }
        }

        raise(GitError.BundledCredentialsNotFound(ActiveConfiguration.configuration.git.builtInCredentialsPreference))
    }

    /** Attempts to build a [CredentialsProvider] by querying Git Credential Manager (GCM) for stored credentials.
     * If GCM is not installed or no credentials are found, returns null.
     *
     * @return A [org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider] with the retrieved username and password, or null if not found.
     */
    private fun buildCredentialsProviderFromGitCredentialManager(): Result<UsernamePasswordCredentialsProvider?> = runCatching {
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

    private fun buildCredentialsProviderFromEnvironment(): UsernamePasswordCredentialsProvider? {
        val username = System.getenv("GIT_USERNAME")
        val password = System.getenv("GIT_PASSWORD")

        return if (username == null || password == null) {
            null
        } else {
            UsernamePasswordCredentialsProvider(username, password)
        }
    }

    private fun buildCredentialsProviderFromGhCli(): Result<UsernamePasswordCredentialsProvider?> = runCatching {
        val process = ProcessBuilder("gh", "auth", "token").start()
        val token = process.inputStream.bufferedReader().readText().trim()

        if (token.isBlank())
            null
        else
            UsernamePasswordCredentialsProvider(token, "")
    }

    val sshSessionFactory = object : JschConfigSessionFactory() {
        override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
            session?.setConfig("StrictHostKeyChecking", "ask")
        }

        override fun createDefaultJSch(fs: FS?): JSch? {
            val jsch = super.createDefaultJSch(fs)
            TODO()
//            when {
//                ActiveConfiguration.sshPrivateKeyPathname != null -> jsch.addIdentity(ActiveConfiguration.sshPrivateKeyPathname)
//                File("${ActiveConfiguration.sshPathname}/id_rsa").exists() -> jsch.addIdentity("${ActiveConfiguration.sshPathname}/id_rsa")
//                File("${ActiveConfiguration.sshPathname}/id_ed25519").exists() -> jsch.addIdentity("${ActiveConfiguration.sshPathname}/id_ed25519")
//            }
            return jsch
        }
    }
}