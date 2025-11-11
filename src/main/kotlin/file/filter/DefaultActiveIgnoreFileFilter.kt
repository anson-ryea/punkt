package com.an5on.file.filter

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.buildPathMatchers
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.osType
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

object DefaultActiveIgnoreFileFilter : IOFileFilter {
    private val defaultPathMatchers = buildPathMatchers(
        when (osType) {
            OsType.WINDOWS -> configuration.global.ignoredActiveFilesForWindows
            OsType.DARWIN -> configuration.global.ignoredActiveFilesForDarwin
            OsType.LINUX -> configuration.global.ignoredActiveFilesForLinux
        }
    )

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