package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledCommitOperation.bundledCommit
import com.an5on.git.system.SystemCommitExecutor.systemCommit
import com.an5on.type.BooleanWithAuto

object CommitOperation {
    fun Raise<GitError>.commit(message: String, useBundledGitOption: BooleanWithAuto?) {

        val localPath = configuration.general.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledCommit(
                localPath,
                message
            )
        } else {
            systemCommit(
                localPath,
                message,
            )
        }
    }
}