package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledAddOperation.bundledAdd
import com.an5on.git.system.SystemAddExecutor.systemAdd
import com.an5on.type.BooleanWithAutoAndDefault
import java.nio.file.Path

object AddOperation {
    fun Raise<GitError>.add(targetPath: Path, useBundledGitOption: BooleanWithAutoAndDefault) {
        val localPath = configuration.general.localStatePath
        val useBundledGit = GitUtils.determineSystemOrBundledGit(useBundledGitOption)

        if (useBundledGit) {
            bundledAdd(
                localPath,
                targetPath
            )
        } else {
            systemAdd(
                localPath,
                targetPath
            )
        }
    }
}