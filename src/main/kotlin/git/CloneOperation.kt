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

class CloneOperation(
    useBundledGitOption: BooleanWithAuto,
    private val repositoryPath: Path = configuration.global.localStatePath,
    remoteRepository: String,
    private val initOptions: InitOptions
) : GitOperableWithSystemAndBundled(
    determineUseBundledGit(useBundledGitOption)
) {
    private val remoteRepositoryUrl = parseRepoUrl(remoteRepository, initOptions.ssh)

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

    override fun operateWithSystem(): Either<GitError, Int> = either {
        val args = mutableListOf("clone").apply {
            initOptions.branch?.let { add("--branch=$it") }
            initOptions.depth?.let { add("--depth=$it") }
            add(remoteRepositoryUrl)
            add(repositoryPath.pathString)
        }

        executeSystemGit(args).bind()
    }

    /**
     * Data class representing a repository URL pattern and its corresponding HTTPS and SSH URL templates.
     *
     * @property pattern A [Regex] pattern to match against input repository strings.
     * @property httpsUrlTemplate A template string for constructing the HTTPS URL, with placeholders for regex capture groups.
     * @property sshUrlTemplate A template string for constructing the SSH URL, with placeholders for regex capture groups.
     * @constructor Creates a [RepoPattern] data instance with an input repository string, the expanded HTTPS URL and SSH formatted String.
     */
    private class RepoPattern(val pattern: Regex, val httpsUrlTemplate: String, val sshUrlTemplate: String)

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
     * If the input string does not match any supported patterns, it is returned unchanged.
     *
     * @param input The input repository string to parse.
     * @param ssh A flag indicating whether to return the URL in SSH format. If false, returns in HTTPS format.
     * @return A valid Git repository URL in the specified format, or the original input string if no patterns matched.
     * @see commonPatterns
     */
    fun parseRepoUrl(input: String, ssh: Boolean): String {
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