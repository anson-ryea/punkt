package com.an5on.file


import com.an5on.system.SystemUtils.homePath
import org.apache.commons.codec.digest.Blake3
import java.io.File
import kotlin.io.path.pathString

/**
 * Utility functions for file operations.
 *
 * This object provides helper methods for common file-related tasks.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object FileUtils {
    /**
     * Replaces the tilde (~) in the pathname with the absolute home directory path.
     *
     * This function is useful for expanding user home directory references in pathnames.
     *
     * @param pathname the pathname string that may contain a leading ~
     * @return the pathname with ~ replaced by the absolute home directory path
     */
    fun replaceTildeWithHomeDirPathname(pathname: String): String =
        pathname.replaceFirst("~", homePath.pathString)

    /**
     * Computes the Blake3 hash of the given file and returns it as a hexadecimal string.
     *
     * @param file the file to hash
     * @return the Blake3 hash of the file's contents as a hex string
     */
    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()
}