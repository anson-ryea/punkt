package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.DiffOptions
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.ExistsInBothActiveAndLocalFileFilter
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalUtils.existsInLocal
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.difflib.algorithm.jgit.HistogramDiff
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

/**
 * Handles the diff operation between active and local states.
 *
 * This object provides operations to compute and display differences between files in the active and local states.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object DiffOperation {
    /**
     * The number of context lines to include in unified diffs.
     */
    const val PATCH_CONTEXT_SIZE = 3

    /**
     * Computes and displays diffs for the specified active paths or all existing local files if no paths are provided.
     *
     * @param paths the set of active paths to diff, or null to diff all existing local files
     * @param options the diff options
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.diff(paths: Set<Path>?, options: DiffOptions, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (paths == null || paths.isEmpty()) {
            diffExistingLocal(options, echos)
        } else {
            diffPaths(paths, options, echos)
        }
    }

    /**
     * Computes and displays diffs for the specified set of active paths.
     *
     * @param activePaths the set of active paths to diff
     * @param options the diff options
     * @param echos the echo functions for output
     */
    private fun Raise<PunktError>.diffPaths(activePaths: Set<Path>, options: DiffOptions, echos: Echos) {

        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())
            .and(ExistsInBothActiveAndLocalFileFilter)

        val expandedLocalPaths = activePaths.flatMap { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(true, includeExcludeFilter, true)
        }

        echos.echo(generateUnifiedDiffStringFromFiles(expandedLocalPaths), true, false)
    }

    private fun diffExistingLocal(options: DiffOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())
            .and(ExistsInBothActiveAndLocalFileFilter)

        val existingLocalPaths = localDirAbsPath.expand(true, includeExcludeFilter, true)

        echos.echo(generateUnifiedDiffStringFromFiles(existingLocalPaths), true, false)
    }

    private fun generateUnifiedDiffStringFromFiles(localPaths: Collection<Path>): String {
        return localPaths.mapNotNull { localPath ->
            val activePath = localPath.toActive()

            assert(activePath.exists())

            val localFileAllLines = Files.readAllLines(localPath)
            val activeFileAllLines = Files.readAllLines(activePath)

            val patch = DiffUtils.diff(
                localFileAllLines,
                activeFileAllLines,
                HistogramDiff()
            )

            if (patch.deltas.isEmpty()) {
                null
            } else {
                UnifiedDiffUtils.generateUnifiedDiff(
                    localPath.pathString,
                    activePath.pathString,
                    localFileAllLines,
                    patch,
                    PATCH_CONTEXT_SIZE
                ).joinToString("\n")
            }
        }.joinToString("\n")
    }
}