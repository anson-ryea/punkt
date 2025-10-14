package com.an5on.git.system

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.config
import com.an5on.error.GitError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState

object SystemGitOperation {
    fun Raise<PunktError>.git(args: List<String>): Int {
        ensure(SystemGitUtils.isGitInstalled) {
            GitError.SystemGitNotFound()
        }

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val process = ProcessBuilder("git", *args.toTypedArray())
            .directory(config.general.localStatePath.toFile())
            .inheritIO()
            .start()

        return process.waitFor()
    }
}