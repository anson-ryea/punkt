package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.command.options.InitOptionGroup
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.RepoPattern.parseRepoUrl
import com.an5on.git.bundled.BundledCloneOperation.bundledClone
import com.an5on.git.system.SystemCloneExecutor.systemClone
import com.an5on.type.BooleanWithAuto

object CloneOperation {
    fun Raise<GitError>.clone(repo: String, initOptions: InitOptionGroup, useBundledGitOption: BooleanWithAuto?) {
        val localPath = configuration.general.localStatePath
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