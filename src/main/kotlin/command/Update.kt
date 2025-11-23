package com.an5on.command

import com.an5on.command.Update.globalOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.git.PullOperation
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.core.Context
import com.github.ajalt.clikt.parameters.groups.provideDelegate

/**
 * A command to update the `punkt` local repository by pulling the latest changes from its remote counterpart.
 *
 * This command fetches and integrates changes from the upstream repository. It is configured to automatically
 * rebase the current branch on top of the fetched changes, stash any local modifications before pulling, and
 * update any Git submodules. This ensures the local repository stays current with the remote source.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 * @property globalOptions The global options for the command, such as verbosity and whether to use the bundled Git executable.
 */
object Update : PunktCommand() {
    private val globalOptions by GlobalOptions()

    override fun help(context: Context): String = """
        Update the local state repository by pulling the latest changes from its remote repository.
        
        Examples:
        punkt update
    """

    override suspend fun run() {
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