package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState

/**
 * A generic Git operation that executes an arbitrary command using the system's native Git executable.
 *
 * This class acts as a pass-through for any Git command provided as a list of arguments. It ensures that the
 * command is executed within the context of the `punkt` local repository. This is useful for exposing Git
 * functionality that does not have a dedicated `GitOperable` implementation.
 *
 * @param args The list of arguments for the Git command to be executed (e.g., `listOf("status", "--short")`).
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class GenericOperationWithSystem(
    private val args: List<String>
) : GitOperableWithSystem {
    /**
     * Executes the provided Git command using the system's native `git` executable.
     *
     * This implementation first ensures that the `punkt` local repository exists, and then executes the command
     * specified in the `args` property within that repository's directory.
     *
     * @return An [Either] containing a [PunktError] on failure (e.g., if the local repository is not found) or the
     * process's exit code on success.
     */
    override fun operateWithSystem(): Either<PunktError, Int> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        executeSystemGit(args, configuration.global.localStatePath).bind()
    }
}