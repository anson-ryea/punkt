package com.an5on.states.active

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.active.ActiveUtils.toActive
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object ActiveState {
    val pendingTransactions = mutableSetOf<ActiveTransaction>()

    fun transact(): Either<PunktError, Unit> = either {
        pendingTransactions.forEach{
            it.run().bind()
        }
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

        val activePath = localAbsPath.toActive().bind()
        makeDirs(localAbsPath)

        Files.copy(localAbsPath, activePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(localPath: Path): Either<PunktError, Unit> = either {
        val activePath = localPath.toActive().bind()

        if (activePath.isDirectory() && !activePath.exists()) {
            Files.createDirectories(activePath)
        } else if (!activePath.parent.exists()) {
            Files.createDirectories(activePath.parent)
        }
    }
}