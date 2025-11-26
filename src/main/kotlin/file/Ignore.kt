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
interface Ignore {
    /** The set of glob patterns used to ignore files and directories. */
    val ignorePatterns: Set<String>

    /** The set of [PathMatcher]s created from [ignorePatterns] for the actual path matching. */
    val ignorePathMatchers: Set<PathMatcher>
        get() = buildPathMatchersFromPatterns(ignorePatterns)

    /**
     * Converts a set of string patterns into a set of [PathMatcher] objects.
     *
     * This function performs the following processing:
     * - Normalises all path separators `\` to `/`.
     * - Prepends `**\/` to patterns that do not contain a `/` and do not start with `**`, to match at any directory depth.
     * - Expands `~` to the user's home directory.
     *
     * @param patterns The set of glob pattern strings to convert.
     * @param isForLocal If true, prefixes the pattern with the local state path.
     * @return A set of [PathMatcher] instances ready for matching.
     */
    fun buildPathMatchersFromPatterns(patterns: Set<String>, isForLocal: Boolean = false): Set<PathMatcher> {
        val normalisedPrefixLocal =
            if (isForLocal) configuration.global.localStatePath.pathString.replace('\\', '/') else ""

        return patterns.map { pattern ->
            val normalisedPattern = pattern.replace('\\', '/')

            val fullPattern = if (normalisedPattern.contains('/') || pattern.startsWith("**")) {
                normalisedPrefixLocal + normalisedPattern
            } else {
                "$normalisedPrefixLocal**/$normalisedPattern"
            }.expandTildeWithHomePathname()

            FileSystems.getDefault().getPathMatcher("glob:$fullPattern")
        }.toSet()
    }
}