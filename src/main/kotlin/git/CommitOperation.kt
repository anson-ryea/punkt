package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.config
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledCommitOperation.bundledCommit
import com.an5on.git.system.SystemCloneOperation.systemClone
import com.an5on.type.BooleanWithAutoAndDefault

object CommitOperation {
    fun Raise<GitError>.commit(message: String, useBundledGitOption: BooleanWithAutoAndDefault) {

        val localPath = config.general.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledCommit(
                localPath,
                message
            )
        } else {
            systemClone(
                localPath,
                message,
            )
        }
    }
}