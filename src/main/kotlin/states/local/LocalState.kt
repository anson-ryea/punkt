package com.an5on.states.local

import com.an5on.config.ActiveConfiguration
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object LocalState {
    fun copyFileFromActiveToLocal(activePath: Path) {
        val activeAbsPath = activePath.toAbsolutePath()

        Files.copy(activeAbsPath, getLocalAbsPath(activeAbsPath.relativeTo(ActiveConfiguration.homeDirAbsPath)))
    }

    fun getLocalRelPathname(relPathname: String): String = relPathname.replace(Regex("^\\.(?!/)|(?<=/)\\."), ActiveConfiguration.dotReplacementString)

    fun getLocalAbsPath(relPathname: String): Path = Path(ActiveConfiguration.localDirAbsPathname + "/" + getLocalRelPathname(relPathname))

    fun getLocalAbsPath(relPath: Path): Path = getLocalAbsPath(relPath.pathString)

    fun getLocalFile(relPath: Path): File = File(getLocalAbsPath(relPath).pathString)
}