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
        val prefixLocal = if (isForLocal) configuration.global.localStatePath.pathString else ""
        return patterns.map { pattern ->
            val normalizedPattern = if (pattern.contains('/') || pattern.contains('\\') || pattern.startsWith("**")) {
                prefixLocal + pattern
            } else {
                "$prefixLocal**/$pattern"
            }.expandTildeWithHomePathname()
            FileSystems.getDefault().getPathMatcher("glob:$normalizedPattern")
        }.toSet()
    }
}