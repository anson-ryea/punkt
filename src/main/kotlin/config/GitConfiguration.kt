package com.an5on.config

import com.an5on.git.BundledGitCredentialsProviderType
import com.an5on.system.SystemUtils
import com.an5on.type.BooleanWithAuto
import com.an5on.type.GitOnLocalChange
import kotlinx.serialization.Serializable
import java.nio.file.Path

/**
 * Represents the Git-related configuration settings for the Punkt application.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
@Serializable
data class GitConfiguration(
    val systemGitCommand: String = "git",
    val builtInCredentialsPreference: Set<BundledGitCredentialsProviderType> = setOf(
        BundledGitCredentialsProviderType.GCM,
        BundledGitCredentialsProviderType.GH_CLI,
        BundledGitCredentialsProviderType.ENV,
    ),
    val useBundledGit: BooleanWithAuto = BooleanWithAuto.AUTO,
    val bundledGitName: String = SystemUtils.username,
    val bundledGitEmail: String = "",
    val gitOnLocalChange: GitOnLocalChange = GitOnLocalChange.NONE,
    val gitSshIdentitiesPath: Path = SystemUtils.sshIdentitiesPath,
    val commitMessage: String = $$"[punkt] ${op} at ${date:dd-MM-yyyy'T'HH:mm:ss}",
)
