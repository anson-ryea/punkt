package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import java.nio.file.Path
import kotlin.io.path.Path


/**
 * An implementation of the [Ignore] interface that sources ignore patterns from a `.punktignore` file.
 *
 * This object is responsible for locating and parsing the `.punktignore` file within the application's
 * local state directory. It extracts patterns, ignoring comments and blank lines, to be used for
 * determining which files and directories should be ignored.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object PunktIgnore : Ignore {
    /** The path to the `.punktignore` file, located in the configured local state directory. */
    val ignoreFilePath = Path("${configuration.global.localStatePath}/.punktignore")

    /**
     * Provides the set of ignore patterns by parsing the `.punktignore` file.
     * The file is read each time the patterns are accessed to ensure they are up-to-date.
     */
    override val ignorePatterns
        get() = parse(ignoreFilePath)

    /**
     * Parses the specified ignore file to extract a set of patterns.
     *
     * This function reads the file line by line. It strips comments (content after a '#')
     * and filters out any resulting blank lines.
     *
     * @param ignoreFilePath The [Path] to the ignore file to be parsed.
     * @return A [Set] of pattern strings. Returns an empty set if the file does not exist.
     */
    fun parse(ignoreFilePath: Path): Set<String> {
        val ignoreFile = ignoreFilePath.toFile()

        if (!ignoreFile.exists()) {
            return setOf()
        }

        val lines = ignoreFile
            .readLines()
            .map { it.stripComment() }
            .filterNot { it.isBlank() }
            .toSet()

        return lines
    }


    /**
     * Removes a comment (starting with '#') and trims leading/trailing whitespace from the string.
     * @return The cleaned string.
     */
    fun String.stripComment(): String {
        val commentIndex = indexOf('#')
        return if (commentIndex >= 0) {
            take(commentIndex).trim()
        } else {
            trim()
        }
    }
}