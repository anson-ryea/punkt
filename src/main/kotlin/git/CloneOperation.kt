package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.config
import com.an5on.command.options.InitOptionGroup
import com.an5on.error.GitError
import com.an5on.git.GitRepoPattern.parseRepoUrl
import com.an5on.git.bundled.BundledCloneOperation.bundledClone
import com.an5on.git.system.SystemCloneOperation.systemClone
import com.an5on.type.BooleanWithAutoAndDefault

object CloneOperation {
    fun Raise<GitError>.clone(repo: String, initOptions: InitOptionGroup, useBundledGitOption: BooleanWithAutoAndDefault) {
        val localPath = config.general.localStatePath
        val repoUrl = parseRepoUrl(repo, initOptions.ssh)
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledClone(
                localPath,
                repoUrl,
                initOptions.ssh,
                initOptions.branch,
                initOptions.depth
            )
        } else {
            systemClone(
                localPath,
                repoUrl,
                initOptions.branch,
                initOptions.depth
            )
        }
    }
}