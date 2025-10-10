package com.an5on.states.local

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.dotReplacementString
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.FileError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object LocalUtils {
    val dotFileRegex = when (SystemUtils.osType) {
        OsType.WINDOWS -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    fun Path.toLocal(): Either<LocalError, Path> = either {
        if (!this@toLocal.isAbsolute) {
            localDirAbsPath.resolve(
                this@toLocal.pathString.replace(dotFileRegex, dotReplacementString)
            ).normalize()
        } else if (this@toLocal.startsWith(homeDirAbsPath)) {
            localDirAbsPath.resolve(
                this@toLocal.relativeTo(homeDirAbsPath).pathString
                    .replace(dotFileRegex, dotReplacementString)
            ).normalize()
        } else {
            Path(
                this@toLocal.pathString.replace(dotFileRegex, dotReplacementString)
            ).normalize()
        }
    }

    fun File.toLocal(): Either<LocalError, File> = either {
        this@toLocal.toPath().toLocal().bind().toFile()
    }

    fun Path.isLocal(): Either<LocalError, Boolean> = either {
        this@isLocal.startsWith(localDirAbsPath)
    }

    fun File.isLocal(): Either<LocalError, Boolean> = either {
        this@isLocal.toPath().isLocal().bind()
    }

    fun Path.existsInLocal(): Either<PunktError, Boolean> = either {
        this@existsInLocal.toLocal().bind().exists()
    }

    fun File.existsInLocal(): Either<PunktError, Boolean> = either {
        this@existsInLocal.toPath().existsInLocal().bind()
    }

    fun Path.fileContentEqualsLocal(): Either<PunktError, Boolean> = either {
        ensure(this@fileContentEqualsLocal.exists()) {
            FileError.PathNotFound(this@fileContentEqualsLocal)
        }

        val localPath = this@fileContentEqualsLocal.toLocal().bind()
        ensure(localPath.exists()) {
            LocalError.LocalPathNotFound(this@fileContentEqualsLocal)
        }

        PathUtils.fileContentEquals(this@fileContentEqualsLocal, localPath)
    }

    fun File.contentEqualsLocal(): Either<PunktError, Boolean> = either {
        this@contentEqualsLocal.toPath().fileContentEqualsLocal().bind()
    }
}