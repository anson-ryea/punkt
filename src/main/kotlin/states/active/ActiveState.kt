package com.an5on.states.active

import com.an5on.states.active.ActiveUtils.toActive
import org.apache.commons.io.FileUtils
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
        assert(localPath.isAbsolute && localPath.exists())

        val localFile = localPath.toFile()
        val activeFile = localFile.toActive()

        FileUtils.copyFile(localFile, activeFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
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