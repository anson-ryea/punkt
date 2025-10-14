package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledPushOperation.bundledPush
import com.an5on.git.system.SystemPushExecutor.systemPush
import com.an5on.type.BooleanWithAuto

object PushOperation {
    fun Raise<GitError>.push(force: Boolean = false, useBundledGitOption: BooleanWithAuto) {
        val localPath = configuration.general.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledPush(localPath, force)
        } else {
            systemPush(localPath, force)
        }
    }
}