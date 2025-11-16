package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.expand
import com.an5on.file.FileUtils.expandToLocal
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.PunktIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

/**
 * An operation to list files and directories managed within the `punkt` local repository.
 *
 * This class orchestrates the `list` command's core logic. It provides a view of the files stored in the local
 * state, which serves as the source for activated dotfiles. It can list all tracked files or a specific subset
 * based on the provided paths and filtering options. This is useful for inspecting the contents of the `punkt`
 * repository without navigating the filesystem directly.
 *
 * @param activePaths An optional set of paths in the active state to list. If null, all tracked files are listed.
 * @param globalOptions The global command-line options, influencing output style.
 * @param commonOptions The common options for filtering by inclusion/exclusion patterns.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class ListOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    echos: Echos,
    terminal: Terminal,
) : OperableWithPathsAndExistingLocal(
    activePaths,
    globalOptions,
    commonOptions,
    OptionGroup(),
    echos,
    terminal
) {

    /**
     * Lists the local repository files corresponding to a specified set of paths from the active state.
     *
     * This method is called when the `list` command is given specific path arguments. It performs the following:
     * 1.  Ensures that each provided active path exists within the local repository.
     * 2.  Applies include/exclude filters from the command options.
     * 3.  Expands the given paths to a full list of corresponding files in the local state.
     * 4.  Prints the resulting list of paths to the console, formatted according to the global path style settings.
     *
     * @param paths The set of paths in the active state to list from the local repository.
     * @return An [Either] containing a [PunktError] on failure (e.g., if a path is not found) or [Unit] on success.
     */
    override fun operateWithPaths(paths: Set<Path>): Either<PunktError, Unit> = either {
        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultActiveIgnoreFileFilter)
            .and(PunktIgnoreFileFilter)

        val expandedLocalPaths = paths.flatMap { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(filter)
        }.toSet()

        echos.echoWithVerbosity(
            expandedLocalPaths.toStringInPathStyle(globalOptions.pathStyle),
            true,
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }

    /**
     * Lists all files and directories currently tracked in the local repository.
     *
     * This method is called when the `list` command is run without any specific path arguments. It performs the following:
     * 1.  Applies include/exclude filters from the command options.
     * 2.  Traverses the entire local repository to gather a list of all tracked files.
     * 3.  Prints the resulting list to the console, formatted according to the global path style settings.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operateWithExistingLocal(): Either<PunktError, Unit> = either {
        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultLocalIgnoreFileFilter)

        val existingLocalPaths = configuration.global.localStatePath
            .expand(filter)
            .filterNot { it == configuration.global.localStatePath }
            .toSet()

        echos.echoWithVerbosity(
            existingLocalPaths.toStringInPathStyle(globalOptions.pathStyle),
            existingLocalPaths.isNotEmpty(),
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }
}