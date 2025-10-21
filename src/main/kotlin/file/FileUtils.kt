package com.an5on.file


import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.PathStyle
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.system.SystemUtils.homePath
import org.apache.commons.codec.digest.Blake3
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.io.path.relativeTo

/**
 * Utility functions for file operations.
 *
 * This object provides helper methods for common file-related tasks.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object FileUtils {
    /**
     * Replaces the tilde (~) in the pathname with the absolute home directory path.
     *
     * This function is useful for expanding user home directory references in pathnames.
     *
     * @return the pathname with ~ replaced by the absolute home directory path
     */
    fun String.expandTildeWithHomePathname(): String = replaceFirst("~", homePath.pathString)

    /**
     * Computes the Blake3 hash of the given file and returns it as a hexadecimal string.
     *
     * @param file the file to hash
     * @return the Blake3 hash of the file's contents as a hex string
     */
    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()

    fun Path.toStringInPathStyle(pathStyle: PathStyle): String = when (pathStyle) {
        PathStyle.ABSOLUTE -> this.toActive().pathString

        PathStyle.RELATIVE -> this.toActive().relativeTo(configuration.global.activeStatePath).pathString

        PathStyle.LOCAL_ABSOLUTE -> this.toLocal().pathString

        PathStyle.LOCAL_RELATIVE -> this.toLocal().relativeTo(configuration.global.localStatePath).pathString
    }

    fun Collection<Path>.toStringInPathStyle(pathStyle: PathStyle): String =
        this.sorted()
            .joinToString(separator = "\n") { it.toStringInPathStyle(pathStyle) }

    fun determinePathStyle(pathStyleOption: PathStyle?) = pathStyleOption ?: configuration.global.pathStyle

    fun readPunktIgnore(ignoreFile: File) {

    }
}