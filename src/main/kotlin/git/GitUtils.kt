package com.an5on.git

import com.an5on.config.ActiveConfiguration.config
import com.an5on.git.system.SystemGitUtils
import com.an5on.type.BooleanWithAuto
import com.an5on.type.BooleanWithAutoAndDefault
import org.eclipse.jgit.lib.PersonIdent

object GitUtils {
    fun determineSystemOrBundledGit(useBundledGitOption: BooleanWithAutoAndDefault) = when (useBundledGitOption) {
        BooleanWithAutoAndDefault.TRUE -> true
        BooleanWithAutoAndDefault.FALSE -> false
        BooleanWithAutoAndDefault.AUTO -> !SystemGitUtils.isGitInstalled
        BooleanWithAutoAndDefault.DEFAULT -> {
            when (config.git.useBundledGit) {
                BooleanWithAuto.TRUE -> true
                BooleanWithAuto.FALSE -> false
                BooleanWithAuto.AUTO -> !SystemGitUtils.isGitInstalled
            }
        }
    }

    val bundledIdentity = PersonIdent(config.git.bundledGitName, config.git.bundledGitEmail)
}