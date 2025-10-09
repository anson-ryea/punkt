package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File
import java.nio.file.Path
import kotlin.io.path.isDirectory

object OperationUtil {
    fun expandPathToFiles(path: Path, recursive: Boolean = true, filter: IOFileFilter): Either<PunktError, Set<File>> = either {
        if (!path.isDirectory()) {
            setOf(path.toFile()).filter { filter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                path.toFile(),
                filter,
                if (recursive) filter else null
            ).toSet()
        }
    }

    fun expandPath(path: Path, recursive: Boolean = true, filter: IOFileFilter): Either<PunktError, Set<Path>> = either {
        expandPathToFiles(path, recursive, filter).bind().map { it.toPath() }.toSet()
    }
}