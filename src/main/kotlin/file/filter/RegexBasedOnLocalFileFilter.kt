package com.an5on.file.filter

import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * [IOFileFilter] that accepts files based on whether their local path matches a given regular expression.
 *
 * This filter converts the file to its local equivalent and checks if the path matches the provided [regex].
 *
 * @property regex the regular expression to match against the local file path
 * @see IOFileFilter
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class RegexBasedOnLocalFileFilter(
    val regex: Regex
) : IOFileFilter {
    /**
     * Tests whether the specified file should be accepted.
     *
     * @param file the file to test
     * @return true if the file's local path matches the regex, false otherwise
     */
    override fun accept(file: File?): Boolean {
        return file?.toLocal()?.path?.matches(regex) ?: false
    }

    /**
     * Tests whether the specified file should be accepted based on directory and name.
     *
     * @param dir the directory in which the file was found
     * @param name the name of the file
     * @return true if the file's local path matches the regex, false otherwise
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}