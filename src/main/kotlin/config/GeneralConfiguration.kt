package com.an5on.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import java.nio.file.Path

data class GeneralConfiguration(
    val localStatePath: Path = SystemUtils.homePath.resolve(
        if (SystemUtils.osType == OsType.WINDOWS) {
            "AppData\\Local\\punkt"
        } else {
            ".local/share/punkt"
        }
    ),
    val activeStatePath: Path = SystemUtils.homePath,
    val trackerPath: Path = SystemUtils.homePath.resolve(
        when (SystemUtils.osType) {
            OsType.WINDOWS -> "AppData\\Local\\punkt\\tracked"
            OsType.DARWIN -> "Library/Application Support/punkt/tracked"
            OsType.LINUX -> ".config/punkt/tracked"
        }
    ),
    val sshPath: Path = SystemUtils.homePath.resolve(".ssh"),
    val dotReplacementPrefix: String = "punkt_",
)
