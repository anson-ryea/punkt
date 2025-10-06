package com.an5on.states.local

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration
import com.an5on.config.Configuration
import com.an5on.error.PunktError
import com.an5on.utils.OsType
import com.an5on.utils.SystemUtils
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo
import kotlin.text.contains

object LocalState {
    val dotFileRegex = when (SystemUtils.osType) {
        OsType.WINDOWS -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    val dotReplacementStringRegex = Regex(ActiveConfiguration.dotReplacementString)

    /** Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    fun checkLocalExists() = ActiveConfiguration.localDirAbsPath.exists()

    fun getLocalRelPathname(relPathname: String): String = relPathname.replace(
        dotFileRegex,
        ActiveConfiguration.dotReplacementString)

    fun getLocalAbsPath(relPathname: String): Path = Path(ActiveConfiguration.localDirAbsPathname + "/" + getLocalRelPathname(relPathname))

    fun getLocalAbsPath(relPath: Path): Path = getLocalAbsPath(relPath.pathString)

    fun getLocalFile(relPath: Path): File = getLocalAbsPath(relPath).toFile()

    fun getActiveRelPathname(localPathname: String) = localPathname.replace(dotReplacementStringRegex, ".")

    fun getActiveAbsPath(localPathname: String) = Path(ActiveConfiguration.homeDirAbsPath.pathString + "/" + getActiveRelPathname(localPathname))

    fun getActiveAbsPath(localPath: Path) = getActiveAbsPath(localPath.pathString)

    fun getActiveFile(localPath: Path) = getActiveAbsPath(localPath).toFile()

    fun copyFileFromActiveToLocal(activePath: Path): Either<PunktError, Unit> = either {
        val activeAbsPath = activePath.toAbsolutePath()

        Files.copy(activeAbsPath, getLocalAbsPath(activeAbsPath.relativeTo(ActiveConfiguration.homeDirAbsPath)), StandardCopyOption.REPLACE_EXISTING)
    }

    fun makeDirs(relPath: Path): Either<PunktError, Unit> = either {
        val localFile = getLocalFile(relPath)

        if (relPath.isDirectory()) {
            localFile.mkdirs()
        } else {
            localFile.parentFile.mkdirs()
        }
    }
}