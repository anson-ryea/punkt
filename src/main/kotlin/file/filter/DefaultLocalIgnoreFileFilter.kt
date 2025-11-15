package com.an5on.file.filter

import com.an5on.file.DefaultLocalIgnore
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.PathMatcherFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File

/**
 * An [IOFileFilter] that filters files based on the default local ignore patterns.
 *
 * This filter uses the patterns provided by [DefaultLocalIgnore] to determine whether a file or directory
 * should be excluded. It constructs a composite filter that accepts a file only if it does *not* match any
 * of the ignore patterns.
 *
 * @see IOFileFilter
 * @see DefaultLocalIgnore
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object DefaultLocalIgnoreFileFilter : IOFileFilter {
    /**
     * Retrieves the set of [java.nio.file.PathMatcher] instances from [DefaultLocalIgnore].
     */
    private val defaultPathMatchers
        get() = DefaultLocalIgnore.ignorePathMatchers

    /**
     * Checks whether the specified file should be accepted.
     *
     * A file is accepted if it does not match any of the default local ignore patterns.
     *
     * @param file The file to check.
     * @return `true` if the file is accepted (i.e., not ignored), `false` otherwise.
     */
    override fun accept(file: File?): Boolean = defaultPathMatchers.fold(TrueFileFilter.INSTANCE) { acc, pathMatcher ->
        acc.and(PathMatcherFileFilter(pathMatcher).negate())
    }.accept(file)

    /**
     * Checks whether the specified file should be accepted, based on its parent directory and name.
     *
     * This delegates to `accept(File)`.
     *
     * @param dir The parent directory of the file.
     * @param name The name of the file.
     * @return `true` if the file is accepted, `false` otherwise.
     */
    override fun accept(dir: File?, name: String?) =
        if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
}