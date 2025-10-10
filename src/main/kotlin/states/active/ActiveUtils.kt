package com.an5on.states.active

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.dotReplacementString
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.FileError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object ActiveUtils {
    private val dotReplacementStringRegex = Regex(dotReplacementString)

    fun Path.toActive(): Either<LocalError, Path> = either {
        if (!this@toActive.isAbsolute) {
            homeDirAbsPath.resolve(
                this@toActive.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        } else if (this@toActive.startsWith(localDirAbsPath)) {
            homeDirAbsPath.resolve(
                this@toActive.relativeTo(localDirAbsPath).pathString
                    .replace(dotReplacementStringRegex, ".")
            )
        } else {
            Path(
                this@toActive.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        }
    }

    fun File.toActive(): Either<LocalError, File> = either {
        this@toActive.toPath().toActive().bind().toFile()
    }

    fun Path.existsInActive(): Either<PunktError, Boolean> = either {
        this@existsInActive.toActive().bind().exists()
    }

    fun File.existsInActive(): Either<PunktError, Boolean> = either {
        this@existsInActive.toPath().existsInActive().bind()
    }

    fun Path.fileContentEqualsActive(): Either<PunktError, Boolean> = either {
        ensure(this@fileContentEqualsActive.exists()) {
            FileError.PathNotFound(this@fileContentEqualsActive)
        }

        val activePath = this@fileContentEqualsActive.toActive().bind()
        ensure(activePath.exists()) {
            LocalError.LocalPathNotFound(this@fileContentEqualsActive)
        }

        PathUtils.fileContentEquals(activePath, this@fileContentEqualsActive)
    }

    fun File.contentEqualsActive(): Either<PunktError, Boolean> = either {
        this@contentEqualsActive.toPath().fileContentEqualsActive().bind()
    }
}