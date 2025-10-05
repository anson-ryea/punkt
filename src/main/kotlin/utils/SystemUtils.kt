package com.an5on.utils

object SystemUtils {
    val osType = when {
        System.getProperty("os.name").lowercase().startsWith("windows") -> OsType.WINDOWS
        System.getProperty("os.name").lowercase().startsWith("mac") -> OsType.DARWIN
        else -> OsType.LINUX
    }
}