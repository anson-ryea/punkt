package com.an5on.file.filter

import com.an5on.file.FileUtils.existsInActive
import com.an5on.file.FileUtils.existsInLocal
import com.an5on.file.FileUtils.isLocal
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File

/**
 * An [IOFileFilter] that accepts files and directories if they exist in both the active and local states.
 *
 * This filter checks for the presence of a corresponding entry in the other state:
 * - If the given path is in the local state, it is accepted if a corresponding path exists in the active state.
 * - If the given path is in the active state, it is accepted if a corresponding path exists in the local state.
 *
 * This filter works for both files and directories.
 *
 * @see IOFileFilter
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object ExistsInBothActiveAndLocalFileFilter : IOFileFilter {
    /**
     * Checks if a given file or directory exists in both its active and local states.
     *
     * @param file The file or directory to check. Can be null, in which case `false` is returned.
     * @return `true` if the item exists in both states, `false` otherwise.
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
     * Checks if a given file or directory exists in both its active and local states, specified by its parent directory and name.
     *
     * This delegates to `accept(File)`.
     *
     * @param dir The parent directory of the item.
     * @param name The name of the file or directory.
     * @return `true` if the item is accepted, `false` otherwise.
     */
    override fun accept(dir: File?, name: String?): Boolean {
        return if (dir != null && name != null) {
            accept(File(dir, name))
        } else {
            false
        }
    }
}