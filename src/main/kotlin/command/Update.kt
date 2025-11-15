package com.an5on.command

import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.git.PullOperation
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.parameters.groups.provideDelegate

object Update : PunktCommand() {
    private val globalOptions by GlobalOptions()

    override fun run() {
        if (!LocalState.exists()) {
            handleError(LocalError.LocalNotFound())
            return
        }

        PullOperation(
            globalOptions.useBundledGit,
            rebase = true,
            autoStash = true,
            recurseSubmodules = true
        ).run().fold(
            { handleError(it) },
            {
                echoSuccess(verbosityOption = globalOptions.verbosity)
            }
        )
    }
}