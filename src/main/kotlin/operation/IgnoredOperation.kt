package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.error.PunktError
import com.an5on.file.PunktIgnore
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal

/**
 * An operation to list the ignore patterns that `punkt` uses to exclude files from its operations.
 *
 * This class orchestrates the `ignored` command's core logic. It retrieves the set of default and user-defined
 * ignore patterns from [PunktIgnore] and prints them to the console. This allows users to inspect which
 * file patterns are currently being excluded from tracking and synchronization.
 *
 * @param globalOptions The global command-line options, which influence output verbosity.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class IgnoredOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal,
): Operable {
    /**
     * Executes the operation to list ignore patterns.
     *
     * This method retrieves all active ignore patterns from [PunktIgnore], formats them as a newline-separated
     * string, and prints them to the console, respecting the configured verbosity level.
     *
     * @return An [Either] containing [Unit] on success, as this operation is not expected to fail.
     */
    override fun operate(): Either<PunktError, Unit> = either {
        val message = PunktIgnore.ignorePatterns.joinToString(separator = "\n")

        echos.echoWithVerbosity(
            message,
            message.isNotBlank(),
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }
}