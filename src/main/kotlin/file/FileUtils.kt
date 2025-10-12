package com.an5on.file

import com.an5on.config.ActiveConfiguration
import org.apache.commons.codec.digest.Blake3
import java.io.File
import kotlin.io.path.pathString

object FileUtils {
    fun replaceTildeWithHomeDirPathname(pathname: String): String =
        pathname.replaceFirst("~", ActiveConfiguration.homeDirAbsPath.pathString)

    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()
}