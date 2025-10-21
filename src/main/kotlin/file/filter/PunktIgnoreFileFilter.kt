package com.an5on.file.filter

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.SystemUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.FileSystems
import java.nio.file.PathMatcher
import kotlin.io.path.pathString

object PunktIgnoreFileFilter : IOFileFilter {
    private fun readPunktIgnore(): List<PathMatcher> {
        val ignoreFile = File("${configuration.global.localStatePath}/.punktignore")

        if (!ignoreFile.exists()) {
            return listOf()
        }

        val lines = ignoreFile
            .readLines()
            .map { it.trim() }
            .filterNot { it.isBlank() || it.startsWith("#") }
            .map {
                if (!it.startsWith("/")) {
                    SystemUtils.homePath.pathString + it
                } else {
                    it
                }
            }

        return lines.map { pattern ->
            FileSystems.getDefault().getPathMatcher("glob:$pattern")
        }
    }

    override fun accept(file: File?): Boolean {
        val matchers = readPunktIgnore()

        return matchers.fold(TrueFileFilter.INSTANCE) { acc, pathMatcher ->
            acc.and(PathMatcherFileFilter(pathMatcher)).negate()
        }.accept(file)
    }

    override fun accept(dir: File?, name: String?) =
        if (dir != null && name != null) {
            ActiveEqualsLocalFileFilter.accept(File(dir, name))
        } else {
            false
        }
}