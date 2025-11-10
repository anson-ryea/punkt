package com.an5on.operation

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.PunktIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

/**
 * Handles the list operation for displaying files in the local state.
 *
 * This object provides operations to list paths, either by listing existing local files or specific paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class ListOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    echos: Echos,
    terminal: Terminal,
) : OperableWithBothPathsAndExistingLocal(
    activePaths,
    globalOptions,
    commonOptions,
    echos,
    terminal
) {

    /**
     * Lists the specified set of active paths.
     *
     * @param activePaths the set of active paths to list
     * @param options the list options
     * @param echo the echo functions for output
     */
    override fun operateWithPaths(paths: Set<Path>) = either<PunktError, Unit> {
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

    override fun operateWithExistingLocal() = either<PunktError, Unit> {
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