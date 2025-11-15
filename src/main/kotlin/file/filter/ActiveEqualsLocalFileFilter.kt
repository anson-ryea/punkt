package com.an5on.file.filter

import com.an5on.file.FileUtils.contentEqualsActive
import com.an5on.file.FileUtils.contentEqualsLocal
import com.an5on.file.FileUtils.isLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * An [IOFileFilter] that accepts files if their content is identical in both the active and local states.
 *
 * This filter is designed to work only with files, not directories. It determines acceptance by comparing
 * the content of a given file with its counterpart in the other state:
 * - If the file is in the local state, it is accepted if its content matches the corresponding active file.
 * - If the file is in the active state, it is accepted if its content matches the corresponding local file.
 *
 * An [IllegalArgumentException] is thrown if a directory is passed to the `accept` method.
 *
 * @see IOFileFilter
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object ActiveEqualsLocalFileFilter : IOFileFilter {
    /**
     * Checks if a file's content is the same in both its active and local states.
     *
     * @param file the file to check. Must not be null or a directory.
     * @return `true` if the file's content is identical in both states, `false` otherwise.
     * @throws IllegalArgumentException if the file is null or a directory.
     */
    override fun accept(file: File?): Boolean {
        if (file == null || file.isDirectory) {
            throw IllegalArgumentException("ActiveEqualsLocalFileFilter only accepts non-directory files.")
        }

        return if (file.isLocal()) {
            file.contentEqualsActive()
        } else {
            file.contentEqualsLocal()
        }
    }

    /**
     * Checks if a file's content is the same in both its active and local states, specified by its parent directory and name.
     *
     * This delegates to `accept(File)`.
     *
     * @param dir the parent directory of the file.
     * @param name the name of the file.
     * @return `true` if the file is accepted, `false` otherwise.
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}