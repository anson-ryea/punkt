package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.PunktError
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.Path

object OperationUtils {
    fun File.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<File>> = either {
        if (!this@expand.isDirectory()) {
            setOf(this@expand).filter { filter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                this@expand,
                filter,
                if (recursive) filter else null
            ).let {
                if (filesOnly) it.filter { file -> file.isFile } else it
            }.toSet()
        }
    }

    fun Path.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<Path>> = either {
        this@expand.toFile().expand(recursive, filter, filesOnly).bind().map { it.toPath() }.toSet()
    }

    fun File.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<File>> = either {
        this@expandToLocal.expand(recursive, filter, filesOnly).bind().map { it.toLocal().bind() }.toSet()
    }

    fun Path.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<Path>> = either {
        this@expandToLocal.toFile().expand(recursive, filter, filesOnly).bind().map { it.toPath().toLocal().bind() }.toSet()
    }

    fun File.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<File>> = either {
        this@expandToActive.expand(recursive, filter, filesOnly).bind().map { it.toActive().bind() }.toSet()
    }

    fun Path.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Either<PunktError, Set<Path>> = either {
        this@expandToActive.toFile().expand(recursive, filter, filesOnly).bind().map { it.toPath().toActive().bind() }.toSet()
    }

    val existingLocalPathsToActivePaths: Either<PunktError, Set<Path>> = either {
        localDirAbsPath.expandToActive(true, TrueFileFilter.INSTANCE).bind()
    }
}