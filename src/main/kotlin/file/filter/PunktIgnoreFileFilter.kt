package com.an5on.file.filter

import com.an5on.file.PunktIgnore.ignorePathMatchers
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

object PunktIgnoreFileFilter : IOFileFilter {
    val pathMatchers
        get() = ignorePathMatchers

    override fun accept(file: File?): Boolean {
        return pathMatchers.fold(TrueFileFilter.INSTANCE) { acc, pathMatcher ->
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