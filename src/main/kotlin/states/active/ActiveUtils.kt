package com.an5on.states.active

import com.an5on.config.ActiveConfiguration.config
import com.an5on.states.local.LocalUtils.isLocal
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.exists
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

/**
 * Utility functions for working with the active state paths and files.
 *
 * This object provides extension functions to convert local paths to active paths, check existence in active state, and compare file contents.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ActiveUtils {
    private val dotReplacementPrefixRegex = Regex(config.general.dotReplacementPrefix)

    /**
     * Converts this path to its corresponding active path.
     *
     * Replaces dot replacement strings with dots and resolves relative to the home directory.
     *
     * @return the active path equivalent
     */
    fun Path.toActive(): Path {
        assert(!this.isAbsolute || this.isLocal())

        return if (!this.isAbsolute) {
            config.general.activeStatePath.resolve(
                this.pathString.replace(dotReplacementPrefixRegex, ".")
            ).normalize()
        } else if (this.startsWith(config.general.localStatePath)) {
            config.general.activeStatePath.resolve(
                this.relativeTo(config.general.localStatePath).pathString
                    .replace(dotReplacementPrefixRegex, ".")
            )
        } else {
            Path(
                this.pathString.replace(dotReplacementPrefixRegex, ".")
            ).normalize()
        }
    }

    /**
     * Converts this file to its corresponding active file.
     *
     * @return the active file equivalent
     */
    fun File.toActive(): File = this.toPath().toActive().toFile()

    /**
     * Checks if this path exists in the active state.
     *
     * @return true if the active path exists, false otherwise
     */
    fun Path.existsInActive() = this.toActive().exists()

    /**
     * Checks if this file exists in the active state.
     *
     * @return true if the active file exists, false otherwise
     */
    fun File.existsInActive() = this.toPath().existsInActive()

    /**
     * Checks if the content of this path equals the content of its active counterpart.
     *
     * @return true if the contents are equal, false otherwise
     */
    fun Path.fileContentEqualsActive(): Boolean {
        assert(this.exists())

        val activePath = this.toActive()
        assert(activePath.exists())

        return PathUtils.fileContentEquals(activePath, this)
    }

    /**
     * Checks if the content of this file equals the content of its active counterpart.
     *
     * @return true if the contents are equal, false otherwise
     */
    fun File.contentEqualsActive() = this.toPath().fileContentEqualsActive()
}