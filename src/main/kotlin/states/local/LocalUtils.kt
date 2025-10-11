package com.an5on.states.local

import com.an5on.config.ActiveConfiguration.dotReplacementString
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
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

    fun Path.toLocal(): Path =
        if (!this.isAbsolute) {
            localDirAbsPath.resolve(
                this.pathString.replace(dotFileRegex, dotReplacementString)
            ).normalize()
        } else if (this.startsWith(homeDirAbsPath)) {
            localDirAbsPath.resolve(
                this.relativeTo(homeDirAbsPath).pathString
                    .replace(dotFileRegex, dotReplacementString)
            ).normalize()
        } else {
            Path(
                this.pathString.replace(dotFileRegex, dotReplacementString)
            ).normalize()
        }

    fun File.toLocal(): File = this.toPath().toLocal().toFile()

    fun Path.isLocal() = this.startsWith(localDirAbsPath)

    fun File.isLocal() = this.toPath().isLocal()

    fun Path.existsInLocal() = this.toLocal().exists()

    fun File.existsInLocal() = this.toPath().existsInLocal()

    fun Path.fileContentEqualsLocal(): Boolean {
        assert(this.exists()) {

        }

        val localPath = this.toLocal()
        assert(localPath.exists()) {

        }

        return PathUtils.fileContentEquals(this, localPath)
    }

    fun File.contentEqualsLocal() = this.toPath().fileContentEqualsLocal()
}