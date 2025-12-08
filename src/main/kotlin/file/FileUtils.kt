package com.an5on.file


import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.homePath
import com.an5on.system.SystemUtils.osType
import com.an5on.type.PathStyle
import org.apache.commons.codec.digest.Blake3
import org.apache.commons.io.FileUtils
import org.apache.commons.io.file.PathUtils
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.Path
import kotlin.io.path.*

/**
 * A utility object for file operations.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object FileUtils {
    /**
     * Replaces the tilde (`~`) in this pathname with the absolute home directory path.
     *
     * @return the pathname with `~` replaced by the absolute home directory path.
     */
    fun String.expandTildeWithHomePathname(): String =
        replaceFirst("^~".toRegex(), homePath.invariantSeparatorsPathString).replace(
            "/",
            FileSystems.getDefault().separator
        )

    /**
     * Computes the Blake3 hash of the given file and returns it as a hexadecimal string.
     *
     * @param file the file to hash.
     * @return the Blake3 hash of the file's contents as a hexadecimal string.
     */
    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()

    /**
     * Converts this path to a string representation in the specified path style.
     *
     * @param pathStyle the [PathStyle] to use for formatting.
     * @return the formatted path string.
     */
    fun Path.toStringInPathStyle(pathStyle: PathStyle): String = when (pathStyle) {
        PathStyle.ABSOLUTE -> this.toActive().pathString

        PathStyle.RELATIVE -> this.toActive().relativeTo(configuration.global.activeStatePath).pathString

        PathStyle.LOCAL_ABSOLUTE -> this.toLocal().pathString

        PathStyle.LOCAL_RELATIVE -> this.toLocal().relativeTo(configuration.global.localStatePath).pathString
    }

    /**
     * Converts this collection of paths to a sorted, newline-separated string in the specified path style.
     *
     * @param pathStyle the [PathStyle] to use for formatting.
     * @return the formatted string of paths, one per line.
     */
    fun Collection<Path>.toStringInPathStyle(pathStyle: PathStyle): String =
        this.sorted()
            .joinToString(separator = "\n") { it.toStringInPathStyle(pathStyle) }

    /**
     * A regular expression pattern to match dot files, adjusted for the operating system.
     *
     * This pattern identifies files and directories that start with a dot (`.`), handling
     * path separators appropriately for Windows (`\`) and Unix-like systems (`/`).
     */
    val dotPrefixRegex = when (osType) {
        OsType.WINDOWS -> Regex("^\\.(?!\\\\)|(?<=\\\\)\\.")
        else -> Regex("^\\.(?!/)|(?<=/)\\.")
    }

    /**
     * Converts this path to its corresponding local state path.
     *
     * This transformation:
     * - Leaves the path unchanged if it is already local.
     * - Replaces dot prefixes with the configured dot replacement prefix.
     * - Resolves the path relative to the local state directory.
     *
     * @return the local state path equivalent.
     */
    fun Path.toLocal(): Path {
        return when {
            isLocal() -> this
            !isAbsolute -> configuration.global.localStatePath.resolve(
                pathString.replace(dotPrefixRegex, configuration.global.dotReplacementPrefix)
            ).normalize()

            startsWith(configuration.global.activeStatePath) -> configuration.global.localStatePath.resolve(
                relativeTo(configuration.global.activeStatePath).pathString.replace(
                    dotPrefixRegex,
                    configuration.global.dotReplacementPrefix
                )
            ).normalize()

            else -> Path(
                pathString.replace(dotPrefixRegex, configuration.global.dotReplacementPrefix)
            ).normalize()
        }
    }

    /**
     * Converts this file to its corresponding local state file.
     *
     * @return the local state file equivalent.
     */
    fun File.toLocal(): File = this.toPath().toLocal().toFile()

    /**
     * Checks if this path is within the local state directory.
     *
     * @return `true` if the path is within the local state directory, `false` otherwise.
     */
    fun Path.isLocal() = this.startsWith(configuration.global.localStatePath)

    /**
     * Checks if this file is within the local state directory.
     *
     * @return `true` if the file is within the local state directory, `false` otherwise.
     */
    fun File.isLocal() = this.toPath().isLocal()

    /**
     * Checks if this path exists in the local state.
     *
     * @return `true` if the corresponding local path exists, `false` otherwise.
     */
    fun Path.existsInLocal() = this.toLocal().exists()

    /**
     * Checks if this file exists in the local state.
     *
     * @return `true` if the corresponding local file exists, `false` otherwise.
     */
    fun File.existsInLocal() = this.toPath().existsInLocal()

    /**
     * Checks if the content of this path equals the content of its local state counterpart.
     *
     * Both paths must exist.
     *
     * @return `true` if the contents are equal, `false` otherwise.
     */
    fun Path.fileContentEqualsLocal(): Boolean {
        assert(this.exists())

        val localPath = this.toLocal()

        assert(localPath.exists())

        return PathUtils.fileContentEquals(this, localPath)
    }

    /**
     * Checks if the content of this file equals the content of its local state counterpart.
     *
     * @return `true` if the contents are equal, `false` otherwise.
     */
    fun File.contentEqualsLocal() = this.toPath().fileContentEqualsLocal()

    /**
     * A regular expression pattern matching the configured dot replacement prefix.
     */
    private val dotReplacementPrefixRegex = Regex(configuration.global.dotReplacementPrefix)

    /**
     * Converts this path to its corresponding active state path.
     *
     * This transformation:
     * - Leaves the path unchanged if it is not a local path.
     * - Replaces dot replacement prefixes with actual dots.
     * - Resolves the path relative to the active state directory.
     *
     * @return the active state path equivalent.
     */
    fun Path.toActive(): Path {
        return when {
            !isLocal() -> this
            !isAbsolute -> {
                configuration.global.activeStatePath.resolve(
                    pathString.replace(dotReplacementPrefixRegex, ".")
                ).normalize()
            }

            startsWith(configuration.global.localStatePath) -> {
                configuration.global.activeStatePath.resolve(
                    relativeTo(configuration.global.localStatePath).pathString
                        .replace(dotReplacementPrefixRegex, ".")
                )
            }

            else -> {
                Path(
                    pathString.replace(dotReplacementPrefixRegex, ".")
                ).normalize()
            }
        }
    }

    /**
     * Converts this file to its corresponding active state file.
     *
     * @return the active state file equivalent.
     */
    fun File.toActive(): File = this.toPath().toActive().toFile()

    /**
     * Checks if this path exists in the active state.
     *
     * @return `true` if the corresponding active path exists, `false` otherwise.
     */
    fun Path.existsInActive() = this.toActive().exists()

    /**
     * Checks if this file exists in the active state.
     *
     * @return `true` if the corresponding active file exists, `false` otherwise.
     */
    fun File.existsInActive() = this.toPath().existsInActive()

    /**
     * Checks if the content of this path equals the content of its active state counterpart.
     *
     * @return `true` if the contents are equal, `false` otherwise.
     */
    fun Path.contentEqualsActive() = this.toFile().contentEqualsActive()

    /**
     * Checks if the content of this file equals the content of its active state counterpart.
     *
     * Both files must exist.
     *
     * @return `true` if the contents are equal, `false` otherwise.
     */
    fun File.contentEqualsActive(): Boolean {
        assert(this.exists())

        val activeFile = this.toActive()
        assert(activeFile.exists())

        return FileUtils.contentEquals(activeFile, this)
    }

    /**
     * Expands this file or directory into a set of files and directories based on the specified filters.
     *
     * If this is a file, returns the file itself if it matches the filter.
     * If this is a directory, recursively lists all matching files and directories.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only files in the result; otherwise includes both files and directories.
     * @return the set of expanded files and directories.
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
     * Expands this path into a set of paths based on the specified filters.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only file paths in the result.
     * @return the set of expanded paths.
     */
    fun Path.expand(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ): Set<Path> =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath() }.toSet()

    /**
     * Expands this file or directory and converts the results to local state paths.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only files in the result.
     * @return the set of expanded local state paths.
     */
    fun File.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.expand(fileFilter, dirFilter, filesOnly).map { it.toLocal() }.toSet()

    /**
     * Expands this path and converts the results to local state paths.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only file paths in the result.
     * @return the set of expanded local state paths.
     */
    fun Path.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath().toLocal() }.toSet()

    /**
     * Expands this file or directory and converts the results to active state paths.
     *
     * The home directory path is excluded from the results.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only files in the result.
     * @return the set of expanded active state paths.
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
     * Expands this path and converts the results to active state paths.
     *
     * The home directory path is excluded from the results.
     *
     * @param fileFilter the filter to apply to files.
     * @param dirFilter the filter to apply to directories; defaults to [fileFilter].
     * @param filesOnly if `true`, includes only file paths in the result.
     * @return the set of expanded active state paths.
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
     * A set of active state paths corresponding to all existing local state paths.
     *
     * This property expands the local state directory and converts all discovered paths to their
     * active state equivalents, filtered by [DefaultLocalIgnoreFileFilter].
     */
    val existingLocalPathsToActivePaths =
        configuration.global.localStatePath
            .expandToActive(DefaultLocalIgnoreFileFilter)
}