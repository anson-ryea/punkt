package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.operation.OperationUtil.expandPathToFiles
import com.an5on.states.active.ActiveState.toActivePath
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalState.existsInLocal
import com.an5on.states.local.LocalState.toLocalPath
import com.an5on.utils.Echos
import com.an5on.utils.FileUtils.onActive
import com.github.difflib.DiffUtils
import com.github.difflib.UnifiedDiffUtils
import com.github.difflib.algorithm.jgit.HistogramDiff
import org.apache.commons.io.filefilter.RegexFileFilter
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.collections.forEach
import kotlin.io.path.pathString

object DiffOperation {
    fun diff(activePaths: Set<Path>, options: DiffOptions, echos: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        activePaths.forEach {
            ensure(it.existsInLocal().bind()) {
                LocalError.LocalPathNotFound(it)
            }
        }

        val localPaths = activePaths.map { it.toLocalPath().bind() }.toSet()

        diffLocal(localPaths, options, echos).bind()
    }

    fun diffExistingLocal(options: DiffOptions, echos: Echos): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        diffLocal(setOf(localDirAbsPath), options, echos).bind()
    }

    fun diffLocal(localPaths: Set<Path>, options: DiffOptions, echos: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val includeExcludeFilter = RegexFileFilter(options.include.pattern)
            .and(RegexFileFilter(options.exclude.pattern).negate())
            .onActive().bind()

        val accumulatedLocalFiles = localPaths.fold(mutableSetOf<File>()) { acc, localPath ->
            acc.addAll(
                (expandPathToFiles(
                    localPath,
                    options.recursive,
                    includeExcludeFilter
                )).bind().filter { it.isFile }.toSet()
            )
            acc
        }
        
        echos.echo(generateUnifiedDiffStringFromFiles(accumulatedLocalFiles).bind(), true, false)
    }

    fun generateUnifiedDiffStringFromFiles(localFiles: Collection<File>): Either<PunktError, String> = either {
        localFiles.fold("") { acc, localFile ->
            val patch = DiffUtils.diff(
                Files.readAllLines(localFile.toPath()),
                Files.readAllLines(localFile.toPath().toActivePath().bind()),
                HistogramDiff()
            )

            acc + UnifiedDiffUtils.generateUnifiedDiff(
                localFile.toPath().toActivePath().bind().pathString,
                localFile.path,
                Files.readAllLines(localFile.toPath()),
                patch,
                3
            ).joinToString("\n") + "\n"
        }
    }
}