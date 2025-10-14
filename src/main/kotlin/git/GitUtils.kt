package com.an5on.git

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.BooleanWithAuto
import com.an5on.type.BooleanWithAutoAndDefault
import org.eclipse.jgit.lib.PersonIdent

object GitUtils {
    fun determineSystemOrBundledGit(useBundledGitOption: BooleanWithAutoAndDefault) = when (useBundledGitOption) {
        BooleanWithAutoAndDefault.TRUE -> true
        BooleanWithAutoAndDefault.FALSE -> false
        BooleanWithAutoAndDefault.AUTO -> !isGitInstalled
        BooleanWithAutoAndDefault.DEFAULT -> {
            when (configuration.git.useBundledGit) {
                BooleanWithAuto.TRUE -> true
                BooleanWithAuto.FALSE -> false
                BooleanWithAuto.AUTO -> !isGitInstalled
            }
        }
    }

    val bundledIdentity = PersonIdent(configuration.git.bundledGitName, configuration.git.bundledGitEmail)

    val isGitInstalled: Boolean
        get() = try {
            val process = ProcessBuilder("git", "--version")
                .redirectErrorStream(true)
                .start()
            process.waitFor() == 0
        } catch (e: Exception) {
            false
        }
}