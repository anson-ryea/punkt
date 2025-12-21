package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import kotlin.io.path.pathString


/**
 * Defines a contract for determining if a path should be ignored based on a set of patterns.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface Ignorable {
    /** The set of glob patterns used to ignore files and directories. */
    val ignorePatterns: Set<String>

    /** The set of [PathMatcher]s created from [ignorePatterns] for the actual path matching. */
    val ignorePathMatchers: Set<PathMatcher>
        get() = buildPathMatchersFromPatterns(ignorePatterns)

    /**
     * Converts a set of string patterns into a set of [PathMatcher] objects.
     *
     * This function performs
     *
     * @param patterns The set of glob pattern strings to convert.
     * @param isForLocal If true, prefixes the pattern with the local state path.
     * @return A set of [PathMatcher] instances ready for matching.
     */
    fun buildPathMatchersFromPatterns(patterns: Set<String>, isForLocal: Boolean = false): Set<PathMatcher> =
        patterns.map { pattern ->
            val localStatePathstring = configuration.global.localStatePath.pathString

            val fullPattern = if (isForLocal) {
                if (pattern.startsWith('/')) {
                        "glob:${localStatePathstring}${pattern}"
                } else {
                        "glob:${localStatePathstring}/$pattern"
                }
            } else {
                "glob:${pattern.expandTildeWithHomePathname()}"
            }.replace("\\", "/")

            FileSystems.getDefault().getPathMatcher(fullPattern)
        }.toSet()
}