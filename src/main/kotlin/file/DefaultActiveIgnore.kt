package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.osType

object DefaultActiveIgnore : Ignore {
    override val ignorePatterns: Set<String>
        get() = when (osType) {
            OsType.WINDOWS -> configuration.global.ignoredActiveFilesForWindows
            OsType.DARWIN -> configuration.global.ignoredActiveFilesForDarwin
            OsType.LINUX -> configuration.global.ignoredActiveFilesForLinux
        }
}