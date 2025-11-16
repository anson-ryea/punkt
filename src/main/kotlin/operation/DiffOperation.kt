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
import com.an5on.file.FileUtils.toActive
import com.an5on.file.filter.*
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.terminal.Terminal
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.difflib.algorithm.jgit.HistogramDiff
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString

/**
 * An operation to display the differences between files in the active state and their corresponding versions in the
 * local repository.
 *
 * This class orchestrates the `diff` command's core logic. It compares files and directories in the user's
 * filesystem (active state) with the versions stored in the `punkt` local state. It handles:
 * - Filtering files based on include/exclude patterns and ignore rules.
 * - Generating a unified diff for each modified file.
 * - Colourising the diff output for improved readability.
 * - Printing the final diff to the console.
 *
 * It can operate on a specific set of paths or on all files tracked in both the local and active states.
 *
 * @param activePaths An optional set of paths in the active state to diff. If null, all tracked files are diffed.
 * @param globalOptions The global command-line options, influencing output verbosity.
 * @param commonOptions The common options for filtering by inclusion/exclusion patterns.
 * @param echos A set of functions for displaying styled console output.
 * @param terminal The terminal instance for user interaction.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class DiffOperation(
    activePaths: Set<Path>?,
    globalOptions: GlobalOptions,
    commonOptions: CommonOptions,
    echos: Echos,
    terminal: Terminal
) : OperableWithPathsAndExistingLocal(
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
     * Computes and displays differences for a specified set of paths from the active state.
     *
     * This method is called when the `diff` command is given specific path arguments. It performs the following:
     * 1.  Ensures that each provided active path exists within the local repository.
     * 2.  Applies include/exclude filters and ensures files exist in both active and local states.
     * 3.  Expands the given paths to a full list of corresponding files in the local state.
     * 4.  Generates and prints a colourised unified diff for the resulting files.
     *
     * @param paths The set of paths in the active state to diff against their local counterparts.
     * @return An [Either] containing a [PunktError] on failure (e.g., if a path is not found) or [Unit] on success.
     */
    override fun operateWithPaths(paths: Set<Path>): Either<PunktError, Unit> = either {
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

    /**
     * Computes and displays differences for all files tracked in both the local and active states.
     *
     * This method is called when the `diff` command is run without any specific path arguments. It performs the following:
     * 1.  Applies include/exclude filters and ensures files exist in both active and local states.
     * 2.  Traverses the local repository to gather a list of all relevant files.
     * 3.  Generates and prints a colourised unified diff for the resulting files.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operateWithExistingLocal(): Either<PunktError, Unit> = either {
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

    /**
     * Generates a single, colourised unified diff string for a collection of local file paths.
     *
     * For each local path, this function reads its content and the content of its corresponding active file,
     * computes the difference, and formats it as a unified diff. Diffs for all files are concatenated.
     *
     * @param localPaths The collection of local file paths to be diffed against their active counterparts.
     * @return A single string containing all the generated unified diffs, with appropriate colouring.
     */
    private fun generateUnifiedDiffStringFromFiles(localPaths: Collection<Path>) =
        localPaths.mapNotNull { localPath ->
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
                ).joinToString("\n") { colouriseDiffLine(it) }
            }
        }.joinToString("\n")

    /**
     * Applies colour to a single line of a diff output based on its prefix.
     *
     * - Lines starting with `+` are coloured green.
     * - Lines starting with `-` are coloured red.
     * - Lines starting with `@@` are coloured cyan.
     * - Other lines remain unchanged.
     *
     * @param line The diff line to colourise.
     * @return The colourised line as a `Spanned` object.
     */
    private fun colouriseDiffLine(line: String) =
        when {
            line.startsWith("+") -> TextColors.green(line)
            line.startsWith("-") -> TextColors.red(line)
            line.startsWith("@@") -> TextColors.cyan(line)
            else -> line
        }
}