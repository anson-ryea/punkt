package com.an5on.states.active

import com.an5on.config.ActiveConfiguration.dotReplacementString
import com.an5on.config.ActiveConfiguration.homeDirAbsPath
import com.an5on.config.ActiveConfiguration.localDirAbsPath
import com.an5on.states.local.LocalUtils.isLocal
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

object ActiveUtils {
    private val dotReplacementStringRegex = Regex(dotReplacementString)

    fun Path.toActive(): Path {
        assert(!this.isAbsolute || this.isLocal())

        return if (!this.isAbsolute) {
            homeDirAbsPath.resolve(
                this.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        } else if (this.startsWith(localDirAbsPath)) {
            homeDirAbsPath.resolve(
                this.relativeTo(localDirAbsPath).pathString
                    .replace(dotReplacementStringRegex, ".")
            )
        } else {
            Path(
                this.pathString.replace(dotReplacementStringRegex, ".")
            ).normalize()
        }
    }

    fun File.toActive(): File = this.toPath().toActive().toFile()

    fun Path.existsInActive() = this.toActive().exists()

    fun File.existsInActive() = this.toPath().existsInActive()

    fun Path.fileContentEqualsActive(): Boolean {
        assert(this.exists())

        val activePath = this.toActive()
        assert(activePath.exists())

        return PathUtils.fileContentEquals(activePath, this)
    }

    fun File.contentEqualsActive() = this.toPath().fileContentEqualsActive()
}