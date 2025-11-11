package com.an5on.file


import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.homePath
import com.an5on.system.SystemUtils.osType
import com.an5on.type.PathStyle
import org.apache.commons.codec.digest.Blake3
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import java.nio.file.PathMatcher
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
    fun String.expandTildeWithHomePathname(): String =
        if (osType == OsType.WINDOWS) {
            this
        } else {
            replaceFirst("~", homePath.pathString)
        }

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

    fun buildPathMatchers(patterns: Set<String>, isForLocal: Boolean = false): List<PathMatcher> {
        val prefixLocal = if (isForLocal) configuration.global.localStatePath.pathString else ""
        return patterns.map { pattern ->
            val normalizedPattern = if (pattern.contains(File.separatorChar) || pattern.startsWith("**")) {
                prefixLocal + pattern
            } else {
                "$prefixLocal**/$pattern"
            }.expandTildeWithHomePathname()
            FileSystems.getDefault().getPathMatcher("glob:$normalizedPattern")
        }
    }
}