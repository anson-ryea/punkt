package com.an5on.file.filter

import com.an5on.file.FileUtils.toActive
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * [IOFileFilter] that accepts files based on whether their active path matches a given regular expression.
 *
 * This filter converts the file to its active equivalent and checks if the path matches the provided [regex].
 *
 * @property regex the regular expression to match against the active file path
 * @see IOFileFilter
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class RegexBasedOnActiveFileFilter(
    val regex: Regex,
) : IOFileFilter {
    /**
     * Tests whether the specified file should be accepted.
     *
     * @param file the file to test
     * @return true if the file's active path matches the regex, false otherwise
     */
    override fun accept(file: File?): Boolean {
        return file?.toActive()?.path?.matches(regex) ?: false
    }

    /**
     * Tests whether the specified file should be accepted based on directory and name.
     *
     * @param dir the directory in which the file was found
     * @param name the name of the file
     * @return true if the file's active path matches the regex, false otherwise
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}