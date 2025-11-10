package com.an5on.file.filter

import com.an5on.config.ActiveConfiguration.configuration
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.FileSystems

object DefaultLocalIgnoreFileFilter : IOFileFilter {
    private val defaultPathMatchers = listOf(
        "${configuration.global.localStatePath}**/.*",
        "${configuration.global.localStatePath}**/.*/**"
    ).map { pathPattern ->
        FileSystems.getDefault().getPathMatcher("glob:$pathPattern")
    }

    override fun accept(file: File?): Boolean = defaultPathMatchers.fold(TrueFileFilter.INSTANCE) { acc, pathMatcher ->
        acc.and(PathMatcherFileFilter(pathMatcher).negate())
    }.accept(file)

    override fun accept(dir: File?, name: String?) =
        if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
}