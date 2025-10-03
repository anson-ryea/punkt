package com.an5on.utils

import com.an5on.config.Configuration
import org.apache.commons.codec.digest.Blake3
import java.io.File

object FileUtils {
    fun replaceTildeWithAbsPathname(pathname: String): String {
        return if (pathname.startsWith("~")) {
            pathname.replaceFirst("~", Configuration.active.homeDirAbsPathname)
        } else {
            pathname
        }
    }

    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()
}