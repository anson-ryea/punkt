package com.an5on.file


import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.homePath
import com.an5on.system.SystemUtils.osType
import com.an5on.type.PathStyle
import org.apache.commons.codec.digest.Blake3
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
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
            val normalizedPattern = if (pattern.contains('/') || pattern.contains('\\') || pattern.startsWith("**")) {
                prefixLocal + pattern
            } else {
                "$prefixLocal**/$pattern"
            }.expandTildeWithHomePathname()
            FileSystems.getDefault().getPathMatcher("glob:$normalizedPattern")
        }
    }

    /**
     * Expands this file or directory into a set of files and directories based on the filter.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded files and directories
     */
    fun File.expand(fileFilter: IOFileFilter, dirFilter: IOFileFilter? = fileFilter, filesOnly: Boolean = false) =
        if (!this.isDirectory()) {
            setOf(this).filter { fileFilter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                this,
                fileFilter,
                dirFilter
            ).let {
                if (filesOnly) it.filter { file -> file.isFile } else it
            }.toSet()
        }

    /**
     * Expands this path into a set of paths based on the filter.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded paths
     */
    fun Path.expand(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ): Set<Path> =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath() }.toSet()

    /**
     * Expands this file or directory and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded local paths
     */
    fun File.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.expand(fileFilter, dirFilter, filesOnly).map { it.toLocal() }.toSet()

    /**
     * Expands this path and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded local paths
     */
    fun Path.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath().toLocal() }.toSet()

    /**
     * Expands this file or directory and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded active paths
     */
    fun File.expandToActive(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.expand(fileFilter, dirFilter, filesOnly)
            .map { it.toActive() }
            .filterNot { it.toPath() == homePath }
            .toSet()

    /**
     * Expands this path and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded active paths
     */
    fun Path.expandToActive(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.toFile().expandToActive(fileFilter, dirFilter, filesOnly)
            .map { it.toPath() }
            .filterNot { it == homePath }
            .toSet()

    /**
     * A set of active paths corresponding to all existing local paths.
     */
    val existingLocalPathsToActivePaths =
        configuration.global.localStatePath
            .expandToActive(DefaultLocalIgnoreFileFilter)
}