package com.an5on.states.local

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

/**
 * Utility functions for working with the local state paths and files.
 *
 * This object provides extension functions to convert active paths to local paths, check if paths are local, check existence in local state, and compare file contents.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object LocalUtils {
    /**
     * Regex pattern to match dot files, adjusted for the operating system.
     */
    val dotPrefixRegex = when (SystemUtils.osType) {
        OsType.WINDOWS -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    /**
     * Converts this path to its corresponding local path.
     *
     * Replaces dot patterns with dot replacement strings and resolves relative to the local directory.
     *
     * @return the local path equivalent
     */
    fun Path.toLocal(): Path {
        assert(!isAbsolute || !isLocal())

        return if (!this.isAbsolute) {
            configuration.general.localStatePath.resolve(
                this.pathString.replace(dotPrefixRegex, configuration.general.dotReplacementPrefix)
            ).normalize()
        } else if (this.startsWith(configuration.general.activeStatePath)) {
            configuration.general.localStatePath.resolve(
                this.relativeTo(configuration.general.activeStatePath).pathString
                    .replace(dotPrefixRegex, configuration.general.dotReplacementPrefix)
            ).normalize()
        } else {
            Path(
                this.pathString.replace(dotPrefixRegex, configuration.general.dotReplacementPrefix)
            ).normalize()
        }
    }

    /**
     * Converts this file to its corresponding local file.
     *
     * @return the local file equivalent
     */
    fun File.toLocal(): File = this.toPath().toLocal().toFile()

    /**
     * Checks if this path is within the local directory.
     *
     * @return true if the path is local, false otherwise
     */
    fun Path.isLocal() = this.startsWith(configuration.general.localStatePath)

    /**
     * Checks if this file is within the local directory.
     *
     * @return true if the file is local, false otherwise
     */
    fun File.isLocal() = this.toPath().isLocal()

    /**
     * Checks if this path exists in the local state.
     *
     * @return true if the local path exists, false otherwise
     */
    fun Path.existsInLocal() = this.toLocal().exists()

    /**
     * Checks if this file exists in the local state.
     *
     * @return true if the local file exists, false otherwise
     */
    fun File.existsInLocal() = this.toPath().existsInLocal()

    /**
     * Checks if the content of this path equals the content of its local counterpart.
     *
     * @return true if the contents are equal, false otherwise
     */
    fun Path.fileContentEqualsLocal(): Boolean {
        assert(this.exists())

        val localPath = this.toLocal()

        assert(localPath.exists())

        return PathUtils.fileContentEquals(this, localPath)
    }

    /**
     * Checks if the content of this file equals the content of its local counterpart.
     *
     * @return true if the contents are equal, false otherwise
     */
    fun File.contentEqualsLocal() = this.toPath().fileContentEqualsLocal()
}