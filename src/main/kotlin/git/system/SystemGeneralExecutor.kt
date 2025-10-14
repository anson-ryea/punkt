package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.git.GitUtils.isGitInstalled
import com.an5on.states.local.LocalState

object SystemGeneralExecutor: SystemGitExecutor() {
    fun Raise<PunktError>.systemGit(args: List<String>): Int {
        ensure(isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        return execute(args, configuration.general.localStatePath)
    }
}