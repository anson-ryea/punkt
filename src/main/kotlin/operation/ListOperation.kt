package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.options.ListOptions
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtils.expand
import com.an5on.operation.OperationUtils.expandToLocal
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalState
import com.an5on.command.Echos
import com.an5on.file.filter.RegexBasedOnActiveFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.nio.file.Path
import kotlin.io.path.relativeTo

object ListOperation {
    fun list(activePaths: Set<Path>?, options: ListOptions, echo: Echos): Either<PunktError, Unit> =
        either {
            if (activePaths == null || activePaths.isEmpty()) {
                listExistingLocal(options, echo).bind()
            } else {
                listPaths(activePaths, options, echo).bind()
            }
        }

    private fun listPaths(activePaths: Set<Path>, options: ListOptions, echo: Echos): Either<PunktError, Unit> =
        either {
            ensure(LocalState.exists()) {
                LocalError.LocalNotFound()
            }

            val includeExcludeFilter = RegexBasedOnActiveFileFilter(options.include)
                .and(RegexBasedOnActiveFileFilter(options.exclude).negate())

            val expandedLocalPaths = activePaths.fold(mutableSetOf<Path>()) { acc, activePath ->
                acc.addAll(
                    activePath.expandToLocal(true, includeExcludeFilter).bind()
                )
                acc
            }

            printFiles(expandedLocalPaths, options.pathStyle, echo).bind()
        }

    private fun listExistingLocal(options: ListOptions, echos: Echos): Either<PunktError, Unit> = either {
        val existingLocalPaths = localDirAbsPath.expand(true, TrueFileFilter.INSTANCE).bind()

        printFiles(existingLocalPaths, options.pathStyle, echos).bind()
    }

    private fun printFiles(paths: Collection<Path>, pathStyle: PathStyles, echos: Echos): Either<PunktError, Unit> =
        either {
            when (pathStyle) {
                PathStyles.ABSOLUTE -> {
                    echos.echo(
                        paths
                            .map { it.toActive().bind() }
                            .sorted()
                            .joinToString("\n"), true, false)
                }

                PathStyles.RELATIVE -> {
                    echos.echo(
                        paths
                            .map { it.toActive().bind().relativeTo(homeDirAbsPath) }
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
}