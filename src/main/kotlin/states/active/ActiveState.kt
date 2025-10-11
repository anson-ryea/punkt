package com.an5on.states.active

import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.error.LocalError
import com.an5on.states.active.ActiveUtils.toActive
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

object ActiveState {
    val pendingTransactions = mutableSetOf<ActiveTransaction>()

    fun transact() {
        pendingTransactions.forEach {
            it.run()
        }
    }

    fun copyFromLocalToActive(localPath: Path) {
        assert(localPath.exists()) {
            LocalError.LocalPathNotFound(localPath)
        }

        val localAbsPath = if (localPath.isAbsolute) {
            localPath
        } else {
            localDirAbsPath.resolve(localPath).normalize()
        }

        val activePath = localAbsPath.toActive()
        makeDirs(localAbsPath)

        Files.copy(localAbsPath, activePath, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(localPath: Path) {
        val activePath = localPath.toActive()

        if (activePath.isDirectory() && !activePath.exists()) {
            Files.createDirectories(activePath)
        } else if (!activePath.parent.exists()) {
            Files.createDirectories(activePath.parent)
        }
    }
}