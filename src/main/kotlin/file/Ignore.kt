package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import kotlin.io.path.pathString

interface Ignore {
    val ignorePatterns: Set<String>
    val ignorePathMatchers: Set<PathMatcher>
        get() = buildPathMatchersFromPatterns(ignorePatterns)

    fun buildPathMatchersFromPatterns(patterns: Set<String>, isForLocal: Boolean = false): Set<PathMatcher> {
        val normalisedPrefixLocal = if (isForLocal) configuration.global.localStatePath.pathString.replace('\\', '/') else ""

        return patterns.map { pattern ->
            val normalisedPattern = pattern.replace('\\', '/')

            val fullPattern = if (normalisedPattern.contains('/') || pattern.startsWith("**")) {
                normalisedPrefixLocal + normalisedPattern
            } else {
                "$normalisedPrefixLocal**$normalisedPattern"
            }.expandTildeWithHomePathname()

            FileSystems.getDefault().getPathMatcher("glob:$fullPattern")
        }.toSet()
    }
}