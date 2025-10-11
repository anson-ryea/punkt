package com.an5on.states.local

import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.FileError
import com.an5on.error.LocalError
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object LocalState {
    /** Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    fun exists() = localDirAbsPath.exists()

    val pendingTransactions = mutableSetOf<LocalTransaction>()

    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }

    fun copyFileFromActiveToLocal(activePath: Path) {
        assert(activePath.exists()) {
            FileError.PathNotFound(activePath)
        }

        val activeAbsPath = if (activePath.isAbsolute) {
            activePath
        } else {
            homeDirAbsPath.resolve(activePath).normalize()
        }

        val activeFile = activeAbsPath.toFile()
        val localFile = activeFile.toLocal()

        FileUtils.copyFile(activeFile, localFile, StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(activePath: Path) {
        val localPath = activePath.toLocal()

        if (activePath.isDirectory() && !localPath.exists()) {
            Files.createDirectories(localPath)
        } else if (!localPath.parent.exists()) {
            Files.createDirectories(localPath.parent)
        }
    }

    fun delete(localPath: Path) {
        assert(localPath.exists()) {
            LocalError.LocalPathNotFound(localPath)
        }

        if (localPath.isDirectory()) {
            localPath.toFile().deleteRecursively()
        } else {
            Files.delete(localPath)
        }
    }
}