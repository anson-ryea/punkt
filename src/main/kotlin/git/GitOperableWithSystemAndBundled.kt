package com.an5on.git

import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.type.BooleanWithAuto

abstract class GitOperableWithSystemAndBundled(
    protected val useBundledGit: Boolean
) : GitOperableWithBundled, GitOperableWithSystem {
    override fun operate() = either<GitError, Unit> {
        if (useBundledGit) {
            operateWithBundled().bind()
        } else {
            operateWithSystem().bind()
        }
    }

    companion object {
        val isGitInstalled: Boolean
            get() = try {
                val process = ProcessBuilder("git", "--version")
                    .redirectErrorStream(true)
                    .start()
                process.waitFor() == 0
            } catch (e: Exception) {
                false
            }

        @JvmStatic
        protected fun determineUseBundledGit(useBundledGitOption: BooleanWithAuto?) = when (useBundledGitOption) {
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
    }
}