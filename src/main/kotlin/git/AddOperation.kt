package com.an5on.git

import arrow.core.raise.Raise
import com.an5on.config.ActiveConfiguration.config
import com.an5on.error.GitError
import com.an5on.git.bundled.BundledAddOperation.bundledAdd
import com.an5on.git.system.SystemAddOperation.systemAdd
import com.an5on.type.BooleanWithAutoAndDefault
import java.nio.file.Path

object AddOperation {
    fun Raise<GitError>.add(targetPath: Path, useBundledGitOption: BooleanWithAutoAndDefault) {
        val localPath = config.general.localStatePath
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