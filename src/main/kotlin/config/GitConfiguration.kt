package com.an5on.config

import com.an5on.git.bundled.BundledGitCredentialsProviderType
import com.an5on.system.SystemUtils
import com.an5on.type.BooleanWithAuto

/**
 * Represents the Git-related configuration settings for the Punkt application.
 *
 * @property addOnLocalChange Whether to automatically add files to Git when local changes are detected.
 * @property commitOnLocalChange Whether to automatically commit changes when local changes are detected.
 * @property pushOnLocalChange Whether to automatically push changes when local changes are detected.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class GitConfiguration(
    val builtInCredentialsPreference: Set<BundledGitCredentialsProviderType> = setOf(
        BundledGitCredentialsProviderType.GCM,
        BundledGitCredentialsProviderType.GH_CLI,
        BundledGitCredentialsProviderType.ENV,
    ),
    val useBundledGit: BooleanWithAuto = BooleanWithAuto.AUTO,
    val bundledGitName: String = SystemUtils.username,
    val bundledGitEmail: String = "",
    val addOnLocalChange: Boolean = true,
    val commitOnLocalChange: Boolean = true,
    val pushOnLocalChange: Boolean = true,
)
