package com.an5on.states.local

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.FileError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.utils.OsType
import com.an5on.utils.SystemUtils
import org.apache.commons.io.file.PathUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.*

object LocalState {
    val dotFileRegex = when (SystemUtils.osType) {
        OsType.WINDOWS -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    /** Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    fun exists() = localDirAbsPath.exists()

    val pendingTransactions = mutableSetOf<LocalTransaction>()

    fun transact(): Either<PunktError, Unit> = either {
        pendingTransactions.forEach {
            it.run().bind()
        }
    }

    fun Path.toLocalPath(): Either<LocalError, Path> = either {
        if (!this@toLocalPath.isAbsolute) {
            localDirAbsPath.resolve(
                this@toLocalPath.pathString.replace(dotFileRegex, ActiveConfiguration.dotReplacementString)
            ).normalize()
        } else if (this@toLocalPath.startsWith(homeDirAbsPath)) {
            localDirAbsPath.resolve(
                this@toLocalPath.relativeTo(homeDirAbsPath).pathString
                    .replace(dotFileRegex, ActiveConfiguration.dotReplacementString)
            ).normalize()
        } else {
            Path(
                this@toLocalPath.pathString.replace(dotFileRegex, ActiveConfiguration.dotReplacementString)
            ).normalize()
        }
    }

    fun Path.existsInLocal(): Either<PunktError, Boolean> = either {
        this@existsInLocal.toLocalPath().bind().exists()
    }

    fun Path.contentEqualsLocal(): Either<PunktError, Boolean> = either {
        ensure(this@contentEqualsLocal.exists()) {
            FileError.PathNotFound(this@contentEqualsLocal)
        }

        val localPath = this@contentEqualsLocal.toLocalPath().bind()
        ensure(localPath.exists()) {
            LocalError.LocalPathNotFound(this@contentEqualsLocal)
        }

        PathUtils.fileContentEquals(this@contentEqualsLocal, localPath)
    }

    fun copyFileFromActiveToLocal(activePath: Path): Either<PunktError, Unit> = either {
        ensure(activePath.exists()) {
            FileError.PathNotFound(activePath)
        }

        val activeAbsPath = if (activePath.isAbsolute) {
            activePath
        } else {
            homeDirAbsPath.resolve(activePath).normalize()
        }

        val localPath = activePath.toLocalPath().bind()
        makeDirs(activePath)

        Files.copy(activeAbsPath, localPath, StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(activePath: Path): Either<PunktError, Unit> = either {
        val localPath = activePath.toLocalPath().bind()

        if (activePath.isDirectory() && !localPath.exists()) {
            Files.createDirectories(localPath)
        } else if (!localPath.parent.exists()) {
            Files.createDirectories(localPath.parent)
        }
    }

    fun delete(localPath: Path): Either<PunktError, Unit> = either {
        ensure(localPath.exists()) {
            LocalError.LocalPathNotFound(localPath)
        }

        if (localPath.isDirectory()) {
            localPath.toFile().deleteRecursively()
        } else {
            Files.delete(localPath)
        }
    }
}