package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledInitOperation.bundledInit
import com.an5on.git.system.SystemInitExecutor.systemInit
import com.an5on.type.BooleanWithAuto

object InitOperation {
    fun Raise<GitError>.init(useBundledGitOption: BooleanWithAuto?) {
        val localPath = configuration.global.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledInit(localPath)
        } else {
            systemInit(localPath)
        }
    }
}