package com.an5on.states.local

import com.an5on.config.ActiveConfiguration
import com.an5on.config.Configuration
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo
import kotlin.text.contains

object LocalState {
    val dotFileRegex = when {
        Configuration.osName.contains("windows", ignoreCase = true) -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    fun copyFileFromActiveToLocal(activePath: Path) {
        val activeAbsPath = activePath.toAbsolutePath()

        Files.copy(activeAbsPath, getLocalAbsPath(activeAbsPath.relativeTo(ActiveConfiguration.homeDirAbsPath)), StandardCopyOption.REPLACE_EXISTING)
    }

    fun getLocalRelPathname(relPathname: String): String = relPathname.replace(
        dotFileRegex,
        ActiveConfiguration.dotReplacementString)

    fun getLocalAbsPath(relPathname: String): Path = Path(ActiveConfiguration.localDirAbsPathname + "/" + getLocalRelPathname(relPathname))

    fun getLocalAbsPath(relPath: Path): Path = getLocalAbsPath(relPath.pathString)

    fun getLocalFile(relPath: Path): File = File(getLocalAbsPath(relPath).pathString)
}