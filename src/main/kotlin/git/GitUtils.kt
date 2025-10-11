package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration
import com.an5on.error.GitError
import com.an5on.git.GitUtils.remoteRepoPatterns
import com.an5on.git.GitUtils.sshSessionFactory
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session
import org.eclipse.jgit.transport.CredentialsProvider
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig
import org.eclipse.jgit.util.FS
import java.io.File

/**
 * Utility object for Git-related operations.
 *
 * @property remoteRepoPatterns A list of [RepoPattern] instances defining supported repository URL formats.
 * @property sshSessionFactory An SSH session factory that configures Jsch SSH sessions for remote Git operations.
 * @author Anson Ng
 */
object GitUtils {
    /**
     * Data class representing a repository URL pattern and its corresponding HTTPS and SSH URL templates.
     *
     * @property pattern A [Regex] pattern to match against input repository strings.
     * @property httpsUrlTemplate A template string for constructing the HTTPS URL, with placeholders for regex capture groups.
     * @property sshUrlTemplate A template string for constructing the SSH URL, with placeholders for regex capture groups.
     * @constructor Creates a [RepoPattern] data instance with an input repository string, the expanded HTTPS URL and SSH formatted String.
     */
    class RepoPattern(val pattern: Regex, val httpsUrlTemplate: String, val sshUrlTemplate: String)

    val remoteRepoPatterns = listOf(
        RepoPattern(
            Regex("([-0-9A-Za-z]+)"),
            $$"https://github.com/%1$s/dotfiles.git",
            $$"git@github.com:%1$s/dotfiles.git"
        ),
        RepoPattern(
            Regex("([-0-9A-Za-z]+)/([-.0-9A-Z_a-z]+?)(\\.git)?"),
            $$"https://github.com/%1$s/%2$s.git",
            $$"git@github.com:%1$s/%2$s.git"
        ),
        RepoPattern(
            Regex("([-.0-9A-Za-z]+)/([-0-9A-Za-z]+)"),
            $$"https://%1$s/%2$s/dotfiles.git",
            $$"git@%1$s:%2$s/dotfiles.git"
        ),
        RepoPattern(
            Regex("([-0-9A-Za-z]+)/([-0-9A-Za-z]+)/([-.0-9A-Za-z]+)"),
            $$"https://%1$s/%2$s/%3$s.git",
            $$"git@%1$s:%2$s/%3$s.git"
        ),
        RepoPattern(
            Regex("([-.0-9A-Za-z]+)/([-0-9A-Za-z]+)/([-0-9A-Za-z]+)(\\.git)?"),
            $$"https://%1$s/%2$s/%3$s.git",
            $$"git@%1$s:%2$s/%3$s.git"
        ),
        RepoPattern(
            Regex("(https?://)([-.0-9A-Za-z]+)/([-0-9A-Za-z]+)/([-0-9A-Za-z]+)(\\.git)?"),
            $$"%1$s%2$s/%3$s/%4$s.git",
            $$"git@%2$s:%3$s/%4$s.git"
        )
    )

    val sshSessionFactory = object : JschConfigSessionFactory() {
        override fun configure(hc: OpenSshConfig.Host?, session: Session?) {
            session?.setConfig("StrictHostKeyChecking", "ask")
        }

        override fun createDefaultJSch(fs: FS?): JSch? {
            val jsch = super.createDefaultJSch(fs)
            when {
                ActiveConfiguration.sshPrivateKeyPathname != null -> jsch.addIdentity(ActiveConfiguration.sshPrivateKeyPathname)
                File("${ActiveConfiguration.sshPathname}/id_rsa").exists() -> jsch.addIdentity("${ActiveConfiguration.sshPathname}/id_rsa")
                File("${ActiveConfiguration.sshPathname}/id_ed25519").exists() -> jsch.addIdentity("${ActiveConfiguration.sshPathname}/id_ed25519")
            }
            return jsch
        }
    }

    /**
     * Builds a [org.eclipse.jgit.transport.CredentialsProvider] using retrieve stored credentials for HTTPS authentication.
     * Currently, supports:
     * - Git Credential Manager (GCM)
     */
    fun buildCredentialsProvider(): Either<GitError, CredentialsProvider> = either {
        buildCredentialsProviderFromGitCredentialManager().bind()
    }

    /**
     * Parses an input repository string and converts it to a valid Git repository URL in either HTTPS or SSH format.
     * If the input string does not match any supported patterns, it is returned unchanged.
     *
     * @param input The input repository string to parse.
     * @param ssh A flag indicating whether to return the URL in SSH format. If false, returns in HTTPS format.
     * @return A valid Git repository URL in the specified format, or the original input string if no patterns matched.
     * @see remoteRepoPatterns
     */
    fun parseRepoUrl(input: String, ssh: Boolean): String {
        remoteRepoPatterns.forEach {
            val matchResult = it.pattern.matchEntire(input)
            if (matchResult != null) {
                return if (ssh) {
                    String.format(it.sshUrlTemplate, *matchResult.groupValues.drop(1).toTypedArray())
                } else {
                    String.format(it.httpsUrlTemplate, *matchResult.groupValues.drop(1).toTypedArray())
                }
            }
        }
        return input
    }

    /** Attempts to build a [CredentialsProvider] by querying Git Credential Manager (GCM) for stored credentials.
     * If GCM is not installed or no credentials are found, returns null.
     *
     * @return A [org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider] with the retrieved username and password, or null if not found.
     */
    private fun buildCredentialsProviderFromGitCredentialManager(): Either<GitError, CredentialsProvider> = either {
        try {
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
            ensure(!(username == null || password == null)) { GitError.GcmNotSet() }
            UsernamePasswordCredentialsProvider(username, password)
        } catch (e: Exception) {
            throw e
        }
    }
}