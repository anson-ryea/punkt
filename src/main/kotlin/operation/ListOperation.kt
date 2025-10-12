package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.ListOptions
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
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

object ListOperation {
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

        val existingLocalPaths = localDirAbsPath.expand(true, includeExcludeFilter)

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
                        .map { it.toActive().relativeTo(homeDirAbsPath) }
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
                        .map { it.relativeTo(localDirAbsPath) }
                        .sorted()
                        .joinToString("\n"), true, false)
            }
        }
}