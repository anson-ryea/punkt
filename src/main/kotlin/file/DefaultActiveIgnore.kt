package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.system.OsType
import com.an5on.system.SystemUtils.osType


/**
 * An implementation of the [Ignorable] interface that provides default active state ignore patterns based on the operating system.
 *
 * This object retrieves ignore patterns from the global configuration, selecting the appropriate set depending on
 * whether the application is running on Windows, macOS (Darwin), or Linux. This allows for platform-specific
 * ignore rules to be applied to active state files.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object DefaultActiveIgnore : Ignorable {
    override val ignorePatterns: Set<String>
        get() = when (osType) {
            OsType.WINDOWS -> configuration.global.ignoredActiveFilesForWindows
            OsType.DARWIN -> configuration.global.ignoredActiveFilesForDarwin
            OsType.LINUX -> configuration.global.ignoredActiveFilesForLinux
        }
}