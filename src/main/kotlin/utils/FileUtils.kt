package com.an5on.utils

import com.an5on.config.Configuration.homeDirAbsPath

object FileUtils {
    fun replaceTildeWithAbsPath(pathname: String): String {
        return if (pathname.startsWith("~")) {
            pathname.replaceFirst("~", homeDirAbsPath)
        } else {
            pathname
        }
    }
}