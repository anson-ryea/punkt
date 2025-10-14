package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.ListOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalUtils.existsInLocal
import java.nio.file.Path
import kotlin.io.path.relativeTo

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
    fun Raise<PunktError>.list(activePaths: Set<Path>?, options: ListOptions, echo: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            listExistingLocal(options, echo)
        } else {
            listPaths(activePaths, options, echo)
        }
    }

    /**
     * Lists the specified set of active paths.
     *
     * @param activePaths the set of active paths to list
     * @param options the list options
     * @param echo the echo functions for output
     */
    private fun Raise<PunktError>.listPaths(activePaths: Set<Path>, options: ListOptions, echo: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())

        val expandedLocalPaths = activePaths.flatMap { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(true, includeExcludeFilter)
        }.toSet()

        printFiles(expandedLocalPaths, options.pathStyle, echo)
    }

    private fun listExistingLocal(options: ListOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())

        val existingLocalPaths = configuration.general.localStatePath.expand(true, includeExcludeFilter)

        printFiles(existingLocalPaths, options.pathStyle, echos)
    }

    private fun printFiles(paths: Collection<Path>, pathStyle: PathStyles, echos: Echos) =
        when (pathStyle) {
            PathStyles.ABSOLUTE -> {
                echos.echo(
                    paths
                        .map { it.toActive() }
                        .sorted()
                        .joinToString("\n"), true, false)
            }

            PathStyles.RELATIVE -> {
                echos.echo(
                    paths
                        .map { it.toActive().relativeTo(configuration.general.activeStatePath) }
                        .sorted()
                        .joinToString("\n"), true, false)
            }

            PathStyles.LOCAL_ABSOLUTE -> {
                echos.echo(
                    paths
                        .sorted()
                        .joinToString("\n"), true, false
                )
            }

            PathStyles.LOCAL_RELATIVE -> {
                echos.echo(
                    paths
                        .map { it.relativeTo(configuration.general.localStatePath) }
                        .sorted()
                        .joinToString("\n"), true, false)
            }
        }
}