package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.DiffOptions
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalState
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.difflib.algorithm.jgit.HistogramDiff
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.pathString

object DiffOperation {
    fun Raise<PunktError>.diff(paths: Set<Path>?, options: DiffOptions, echos: Echos) =
        if (paths == null || paths.isEmpty()) {
            diffExistingLocal(options, echos)
        } else {
            diffPaths(paths, options, echos)
        }

    private fun Raise<PunktError>.diffPaths(activePaths: Set<Path>, options: DiffOptions, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())

        val expandedLocalPaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
            acc.addAll(
                activePath.expandToLocal(true, includeExcludeFilter, true)
            )
            acc
        }

        echos.echo(generateUnifiedDiffStringFromFiles(expandedLocalPaths), true, false)
    }

    private fun diffExistingLocal(options: DiffOptions, echos: Echos) {
        val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
            .and(RegexBasedOnActiveFileFilter(options.exclude).negate())

        val existingLocalPaths = localDirAbsPath.expand(true, includeExcludeFilter, true)

        echos.echo(generateUnifiedDiffStringFromFiles(existingLocalPaths), true, false)
    }

    private fun generateUnifiedDiffStringFromFiles(localPaths: Collection<Path>) {
        localPaths.mapNotNull { localPath ->
            val activePath = localPath.toActive()

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
                    3
                ).joinToString("\n")
            }
        }.joinToString("\n")
    }
}