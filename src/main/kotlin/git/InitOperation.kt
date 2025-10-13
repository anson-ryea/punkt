package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.config
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledInitOperation.bundledInit
import com.an5on.git.system.SystemInitOperation.systemInit
import com.an5on.type.BooleanWithAutoAndDefault

object InitOperation {
    fun Raise<GitError>.init(useBundledGitOption: BooleanWithAutoAndDefault) {
        val localPath = config.general.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledInit(localPath)
        } else {
            systemInit(localPath)
        }
    }
}