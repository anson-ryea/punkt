package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.determinePathStyle
import com.an5on.file.FileUtils.toStringInPathStyle
import com.an5on.file.filter.DefaultActiveIgnoreFileFilter
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.file.filter.PunktIgnoreFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.type.Verbosity
import java.nio.file.Path

/**
 * Handles the list operation for displaying files in the local state.
 *
 * This object provides operations to list paths, either by listing existing local files or specific paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ListOperation {
    /**
     * Lists the specified active paths or all existing local files if no paths are provided.
     *
     * @param activePaths the set of active paths to list, or null to list all existing local files
     * @param options the list options
     * @param echo the echo functions for output
     */
    fun Raise<PunktError>.list(activePaths: Set<Path>?, globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            listExistingLocal(globalOptions, commonOptions, echos)
        } else {
            listPaths(activePaths, globalOptions, commonOptions, echos)
        }
    }

    /**
     * Lists the specified set of active paths.
     *
     * @param activePaths the set of active paths to list
     * @param options the list options
     * @param echo the echo functions for output
     */
    private fun Raise<PunktError>.listPaths(activePaths: Set<Path>, globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultActiveIgnoreFileFilter)
            .and(PunktIgnoreFileFilter)

        val expandedLocalPaths = activePaths.flatMap { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(true, includeExcludeFilter)
        }.toSet()

        val pathStyle = determinePathStyle(globalOptions.pathStyle)
        echos.echoWithVerbosity(
            expandedLocalPaths.toStringInPathStyle(pathStyle),
            true,
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }

    private fun listExistingLocal(globalOptions: GlobalOptions, commonOptions: CommonOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultLocalIgnoreFileFilter)

        val existingLocalPaths = configuration.global.localStatePath
            .expand(true, includeExcludeFilter)
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