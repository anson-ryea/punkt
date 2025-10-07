package com.an5on.states.active

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.FileError
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object ActiveState {
    val dotReplacementStringRegex = Regex(ActiveConfiguration.dotReplacementString)

    val pendingTransactions = mutableSetOf<ActiveTransaction>()

    fun transact(): Either<PunktError, Unit> = either {
        pendingTransactions.forEach{
            it.run().bind()
        }
    }

    fun Path.toActivePath(): Either<LocalError, Path> = either {
        if (!this@toActivePath.isAbsolute) {
            homeDirAbsPath.resolve(
                this@toActivePath.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        } else if (this@toActivePath.startsWith(localDirAbsPath)) {
            homeDirAbsPath.resolve(
                this@toActivePath.relativeTo(localDirAbsPath).pathString
                    .replace(dotReplacementStringRegex, ".")
            )
        } else {
            Path(
                this@toActivePath.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        }
    }

    fun Path.existsInActive(): Either<PunktError, Boolean> = either {
        this@existsInActive.toActivePath().bind().exists()
    }

    fun Path.contentEqualsActive(): Either<PunktError, Boolean> = either {
        ensure(this@contentEqualsActive.exists()) {
            FileError.PathNotFound(this@contentEqualsActive)
        }

        val localFile = this@contentEqualsActive.toFile()

        val activeFile = this@contentEqualsActive.toActivePath().bind().toFile()
        ensure(activeFile.exists()) {
            LocalError.LocalPathNotFound(this@contentEqualsActive)
        }

        FileUtils.contentEquals(activeFile, localFile)
    }

    fun copyFromLocalToActive(localPath: Path): Either<PunktError, Unit> = either {
        ensure(localPath.exists()) {
            LocalError.LocalPathNotFound(localPath)
        }

        val localAbsPath = if (localPath.isAbsolute) {
            localPath
        } else {
            localDirAbsPath.resolve(localPath).normalize()
        }

        val activePath = localAbsPath.toActivePath().bind()
        makeDirs(localAbsPath)

        Files.copy(localAbsPath, activePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(localPath: Path): Either<PunktError, Unit> = either {
        val activePath = localPath.toActivePath().bind()

        if (activePath.isDirectory() && !activePath.exists()) {
            Files.createDirectories(activePath)
        } else if (!activePath.parent.exists()) {
            Files.createDirectories(activePath.parent)
        }
    }
}