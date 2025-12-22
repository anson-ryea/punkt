package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.options.InitOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.GitOperableWithBundled.Companion.buildCredentialsProvider
import com.an5on.git.GitOperableWithBundled.Companion.sshSessionFactory
import com.an5on.type.BooleanWithAuto
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.errors.GitAPIException
import org.eclipse.jgit.transport.SshTransport
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * A Git operation to clone a repository into a new directory.
 *
 * This class implements the `git clone` command. It can operate using either the bundled JGit library or the
 * system's native Git executable. The operation clones a `remoteRepository` into the specified `repositoryPath`,
 * with options for specifying a branch, clone depth, and connection protocol (HTTPS/SSH).
 *
 * It also includes logic to parse shorthand repository URLs into full HTTPS or SSH URLs.
 *
 * @param useBundledGitOption An option to determine whether to use the bundled JGit (`TRUE`), the system Git (`FALSE`),
 * or to auto-detect (`AUTO`).
 * @param repositoryPath The file system path where the repository will be cloned. Defaults to the `localStatePath`
 * from the application's configuration.
 * @param remoteRepository The URL or shorthand for the remote repository to clone.
 * @param initOptions A set of options for the clone operation, such as branch, depth, and SSH flag.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class CloneOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    remoteRepository: String,
    private val initOptions: InitOptions
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val remoteRepositoryUrl = parseRepoUrl(remoteRepository, initOptions.ssh)

    /**
     * Clones a repository using the bundled JGit library.
     *
     * This implementation configures the clone command with the repository URL, target directory, and any
     * specified options like branch and depth. It also sets up the appropriate credentials provider for HTTPS
     * or configures the transport for SSH.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    override fun operateWithBundled(): Either<GitError, Unit> = either {

        try {
            Git.cloneRepository().apply {
                setDirectory(repositoryPath.toFile())
                setURI(remoteRepositoryUrl)

                initOptions.branch?.let { setBranch(it) }
                initOptions.depth?.let { setDepth(it) }

                if (initOptions.ssh) {
                    setTransportConfigCallback { transport ->
                        if (transport is SshTransport) {
                            transport.sshSessionFactory = sshSessionFactory
                        }
                    }
                } else {
                    setCredentialsProvider(buildCredentialsProvider().bind())
                }
            }.call()
        } catch (e: GitAPIException) {
            raise(GitError.BundledGitOperationFailed("Clone", e))
        }
    }

    /**
     * Clones a repository using the system's native `git` command.
     *
     * This implementation builds and executes a `git clone` command with the appropriate command-line arguments
     * for the branch and depth, based on the properties of the class.
     *
     * @return An [Either] containing a [GitError] on failure or the process's exit code on success.
     */
    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = mutableListOf("clone").apply {
            initOptions.branch?.let { add("--branch=$it") }
            initOptions.depth?.let { add("--depth=$it") }
            add(remoteRepositoryUrl)
            add(repositoryPath.pathString)
        }

        executeSystemGit(args).bind()
    }

    companion object {
        /**
         * A data class representing a repository URL pattern and its corresponding HTTPS and SSH URL templates.
         *
         * This is used to convert shorthand repository identifiers (like `user/repo`) into full, cloneable Git URLs.
         *
         * @property pattern A [Regex] pattern to match against input repository strings.
         * @property httpsUrlTemplate A template string for constructing the HTTPS URL, with placeholders for regex capture groups.
         * @property sshUrlTemplate A template string for constructing the SSH URL, with placeholders for regex capture groups.
         */
        private data class RepoPattern(val pattern: Regex, val httpsUrlTemplate: String, val sshUrlTemplate: String)

        /**
         * A list of predefined [RepoPattern]s used to parse shorthand repository URLs.
         *
         * The patterns are tried in order, and the first one that matches the input string is used to generate the
         * final URL. This allows for flexible repository identifiers, such as `username`, `username/repo`, etc.
         */
        private val commonPatterns = listOf(
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

        /**
         * Parses an input repository string and converts it to a valid Git repository URL in either HTTPS or SSH format.
         *
         * This function iterates through the [commonPatterns] and applies them to the input string. If a match is found,
         * it formats the corresponding URL template (HTTPS or SSH) with the captured groups. If no patterns match,
         * the original input string is returned, assuming it is already a valid URL.
         *
         * @param input The input repository string to parse (e.g., "my-user/my-repo").
         * @param ssh A flag indicating whether to return the URL in SSH format (`true`) or HTTPS format (`false`).
         * @return A valid Git repository URL in the specified format, or the original input string if no patterns matched.
         */
        private fun parseRepoUrl(input: String, ssh: Boolean): String {
            commonPatterns.forEach {
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
    }
}