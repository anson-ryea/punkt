package com.an5on.states.local

import com.an5on.config.Configuration
import com.an5on.utils.FileUtils.replaceTildeWithAbsPathname
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object LocalState {
    fun copyFileFromActiveToLocal(activePathname: String) {
        val activePathnameWithTildeRemoved = replaceTildeWithAbsPathname(activePathname)
        val activeAbsPath = Path(activePathnameWithTildeRemoved).toAbsolutePath()

        Files.copy(activeAbsPath, getLocalPath(activeAbsPath.relativeTo(Configuration.active.homeDirAbsPath).pathString))
    }

    fun getLocalRelPathname(relPathname: String): String = relPathname.replace(Regex("^\\.(?!/)|(?<=/)\\."), Configuration.active.dotReplacementString)

    fun getLocalPath(relPathname: String): Path = Path(Configuration.active.localDirAbsPathname + "/" + getLocalRelPathname(relPathname))
}