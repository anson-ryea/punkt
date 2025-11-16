package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

/**
 * An abstract base class for operations that require an existing `punkt` local repository and can operate
 * either on a specific set of paths or on all tracked files.
 *
 * This class simplifies the implementation of commands that have two modes of operation:
 * 1.  Targeted: Acting on a user-provided list of file or directory paths.
 * 2.  Global: Acting on all files currently tracked within the local repository.
 *
 * It ensures that a local repository exists before proceeding and then delegates to one of two abstract methods
 * based on whether a list of paths was provided.
 *
 * @property activePaths An optional set of paths in the active state to operate on. If null or empty, the operation
 * will apply to all tracked files in the local state.
 * @property globalOptions The global command-line options.
 * @property commonOptions The common command-line options for filtering and recursion.
 * @property specificOptions A group of options specific to the implementing command.
 * @property echos A set of functions for displaying styled console output.
 * @property terminal The terminal instance for user interaction.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
abstract class OperableWithPathsAndExistingLocal(
    protected val activePaths: Set<Path>?,
    protected val globalOptions: GlobalOptions,
    protected val commonOptions: CommonOptions,
    protected val specificOptions: OptionGroup,
    protected val echos: Echos,
    protected val terminal: Terminal,
) : Operable {
    /**
     * The main execution logic for the operation.
     *
     * This method first ensures that the `punkt` local repository exists. It then checks if a set of `activePaths`
     * has been provided.
     * - If paths are provided, it calls [operateWithPaths].
     * - If no paths are provided, it calls [operateWithExistingLocal].
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operate() = either<PunktError, Unit> {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            operateWithExistingLocal()
        } else {
            operateWithPaths(activePaths)
        }
    }

    /**
     * Abstract method to define the operation's logic when no specific paths are provided.
     *
     * Implementing classes should provide the logic for operating on all relevant files within the existing
     * local repository.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    abstract fun operateWithExistingLocal(): Either<PunktError, Unit>

    /**
     * Abstract method to define the operation's logic when a specific set of paths is provided.
     *
     * Implementing classes should provide the logic for operating on the given set of `paths`.
     *
     * @param paths The set of paths in the active state to operate on.
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    abstract fun operateWithPaths(paths: Set<Path>): Either<PunktError, Unit>
}