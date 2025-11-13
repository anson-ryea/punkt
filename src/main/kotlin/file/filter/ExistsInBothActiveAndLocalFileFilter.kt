package com.an5on.file.filter

import com.an5on.file.FileUtils.existsInActive
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.isLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * [IOFileFilter] that accepts files that exist in both the active and local states.
 *
 * This filter determines acceptance based on whether the file is in the local state or active state:
 * - For local files: Accepts if the corresponding file exists in the active state.
 * - For active files: Accepts if the corresponding file exists in the local state.
 *
 * @see IOFileFilter
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ExistsInBothActiveAndLocalFileFilter : IOFileFilter {
    /**
     * Tests whether the specified file should be accepted.
     *
     * @param file the file to test
     * @return true if the file is accepted, false otherwise
     */
    override fun accept(file: File?): Boolean {
        if (file == null) {
            return false
        }

        return if (file.isLocal()) {
            file.existsInActive()
        } else {
            file.existsInLocal()
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