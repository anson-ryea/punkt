package com.an5on.operation

import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.expand
import com.an5on.file.FileUtils.expandToLocal
import com.an5on.file.filter.*
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.terminal.Terminal
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
class DiffOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    echos: Echos,
    terminal: Terminal
) : OperableWithBothPathsAndExistingLocal(
    activePaths,
    globalOptions,
    commonOptions,
    OptionGroup(),
    echos,
    terminal
) {
    /**
     * The number of context lines to include in unified diffs.
     */
    private val patchContextSize = 3

    /**
     * Computes and displays diffs for the specified set of active paths.
     *
     * @param activePaths the set of active paths to diff
     * @param options the diff options
     * @param echos the echo functions for output
     */
    override fun operateWithPaths(paths: Set<Path>) = either<PunktError, Unit> {
        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultActiveIgnoreFileFilter)
            .and(PunktIgnoreFileFilter)
            .and(ExistsInBothActiveAndLocalFileFilter)

        val expandedLocalPaths = paths.flatMap { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.expandToLocal(filter, filesOnly = true)
        }

        val unifiedDiff = generateUnifiedDiffStringFromFiles(expandedLocalPaths)
        echos.echoWithVerbosity(
            unifiedDiff,
            unifiedDiff.isNotBlank(),
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }

    override fun operateWithExistingLocal() = either<PunktError, Unit> {
        val filter = RegexBasedOnActiveFileFilter(commonOptions.include)
            .and(RegexBasedOnActiveFileFilter(commonOptions.exclude).negate())
            .and(DefaultLocalIgnoreFileFilter)
            .and(ExistsInBothActiveAndLocalFileFilter)

        val existingLocalPaths = configuration.global.localStatePath.expand(filter, filesOnly = true)

        val unifiedDiff = generateUnifiedDiffStringFromFiles(existingLocalPaths)
        echos.echoWithVerbosity(
            unifiedDiff,
            unifiedDiff.isNotBlank(),
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
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
                    patchContextSize
                ).joinToString("\n")
            }
        }.joinToString("\n")
    }
}