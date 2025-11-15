package com.an5on.config

import com.an5on.git.CredentialsProviderForBundledType
import com.an5on.system.SystemUtils
import com.an5on.type.BooleanWithAuto
import com.an5on.type.GitOnLocalChange
import kotlinx.serialization.Serializable
import java.nio.file.Path

/**
 * Represents the Git-related configuration settings for the Punkt application.
 *
 * This data class holds all settings related to Git operations, such as which Git executable to use,
 * how to handle credentials, and the format for commit messages.
 *
 * @property systemGitCommand The command or path to the system's Git executable. Defaults to "git".
 * @property builtInCredentialsPreference The preferred order of credential providers for the bundled Git implementation.
 * @property useBundledGit Determines whether to use the bundled Git implementation or the system's Git.
 * @property bundledGitName The name to use for commits made with the bundled Git. Defaults to the system username.
 * @property bundledGitEmail The email to use for commits made with the bundled Git.
 * @property gitOnLocalChange Defines the automatic Git action to perform when local files are changed.
 * @property gitSshIdentitiesPath The path to the directory containing SSH identities for Git authentication.
 * @property commitMessage A template for the commit message used for automatic commits.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class GitConfiguration(
    val systemGitCommand: String = "git",
    val builtInCredentialsPreference: Set<CredentialsProviderForBundledType> = setOf(
        CredentialsProviderForBundledType.GCM,
        CredentialsProviderForBundledType.GH_CLI,
        CredentialsProviderForBundledType.ENV,
    ),
    val useBundledGit: BooleanWithAuto = BooleanWithAuto.AUTO,
    val bundledGitName: String = SystemUtils.username,
    val bundledGitEmail: String = "",
    val gitOnLocalChange: GitOnLocalChange = GitOnLocalChange.NONE,
    val gitSshIdentitiesPath: Path = SystemUtils.sshIdentitiesPath,
    val commitMessage: String = $$"[punkt] ${op} at ${date:dd-MM-yyyy'T'HH:mm:ss}",
)
