package com.an5on.file

import com.an5on.config.ActiveConfiguration
import org.apache.commons.codec.digest.Blake3
import java.io.File
import kotlin.io.path.pathString

object FileUtils {
    fun replaceTildeWithAbsPathname(pathname: String): String {
        return if (pathname.startsWith("~")) {
            pathname.replaceFirst("~", ActiveConfiguration.homeDirAbsPath.pathString)
        } else {
            pathname
        }
    }

    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()
}