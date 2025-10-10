package com.an5on.system

object SystemUtils {
    val osType = when {
        System.getProperty("os.name").lowercase().startsWith("windows") -> OsType.WINDOWS
        System.getProperty("os.name").lowercase().startsWith("mac") -> OsType.DARWIN
        else -> OsType.LINUX
    }
}