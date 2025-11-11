package com.an5on.git

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.BooleanWithAuto
import org.apache.commons.text.StringSubstitutor
import org.eclipse.jgit.lib.PersonIdent

object GitUtils {
    val isGitInstalled: Boolean
        get() = try {
            val process = ProcessBuilder("git", "--version")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }

    fun determineSystemOrBundledGit(useBundledGitOption: BooleanWithAuto?) = when (useBundledGitOption) {
        BooleanWithAuto.TRUE -> true
        BooleanWithAuto.FALSE -> false
        BooleanWithAuto.AUTO -> !isGitInstalled
        null -> {
            when (configuration.git.useBundledGit) {
                BooleanWithAuto.TRUE -> true
                BooleanWithAuto.FALSE -> false
                BooleanWithAuto.AUTO -> !isGitInstalled
            }
        }
    }

    val bundledIdentity = PersonIdent(configuration.git.bundledGitName, configuration.git.bundledGitEmail)

    fun substituteCommitMessage(commitMessage: String, operationName: String): String {
        val interpolator = StringSubstitutor.createInterpolator()
        val interpolatedMessage = interpolator.replace(commitMessage)
        val substitutor = StringSubstitutor(
            mapOf(
                "op" to operationName
            )
        )
        val substitutedMessage = substitutor.replace(interpolatedMessage)
        return substitutedMessage
    }
}