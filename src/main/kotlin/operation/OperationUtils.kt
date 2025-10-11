package com.an5on.operation

import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.Path

object OperationUtils {
    fun File.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        if (!this.isDirectory()) {
            setOf(this).filter { filter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                this,
                filter,
                if (recursive) filter else null
            ).let {
                if (filesOnly) it.filter { file -> file.isFile } else it
            }.toSet()
        }

    fun Path.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Set<Path> =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath() }.toSet()

    fun File.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.expand(recursive, filter, filesOnly).map { it.toLocal() }.toSet()

    fun Path.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath().toLocal() }.toSet()

    fun File.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.expand(recursive, filter, filesOnly).map { it.toActive() }.toSet()

    fun Path.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath().toActive() }.toSet()

    val existingLocalPathsToActivePaths = localDirAbsPath.expandToActive(true, TrueFileFilter.INSTANCE)

}