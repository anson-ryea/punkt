package com.an5on.file.filter

import com.an5on.states.active.ActiveUtils.contentEqualsActive
import com.an5on.states.local.LocalUtils.contentEqualsLocal
import com.an5on.states.local.LocalUtils.isLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * [IOFileFilter] that accepts files where the file content of both the active and local states are equal.
 *
 * This filter determines acceptance based on whether the file is in the local state or active state:
 * - For local files: Accepts directories if the corresponding active directory exists,
 *   and accepts files if their content equals the active content.
 * - For active files: Accepts directories if the corresponding local directory exists,
 *   and accepts files if their content equals the local content.
 *
 *   @see IOFileFilter
 *   @author Anson Ng <hej@an5on.com>
 *   @since 0.1.0
 */
object ActiveEqualsLocalFileFilter : IOFileFilter {
    /**
     * Tests whether the specified file should be accepted.
     *
     * @param file the file to test
     * @return true if the file is accepted, false otherwise
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
     * Tests whether the specified file should be accepted based on directory and name.
     *
     * @param dir the directory in which the file was found
     * @param name the name of the file
     * @return true if the file is accepted, false otherwise
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}