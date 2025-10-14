package com.an5on.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import java.nio.file.Path

/**
 * Represents the general configuration settings for the Punkt application.
 *
 * @property localStatePath The path to the local state directory where Punkt stores cloned dotfiles.
 * @property activeStatePath The path to the active state directory.
 * @property trackerPath The path to the tracker database directory where tracked file states are stored.
 * @property sshPath The path to the SSH directory for SSH key management.
 * @property dotReplacementPrefix The prefix used to replace dots in file names when storing them in the local state.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
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
            OsType.WINDOWS -> "AppData\\Local\\punkt\\db\\tracked"
            OsType.DARWIN -> "Library/Application Support/punkt/db/tracked"
            OsType.LINUX -> ".config/punkt/db/tracked"
        }
    ),
    val sshPath: Path = SystemUtils.homePath.resolve(".ssh"),
    val dotReplacementPrefix: String = "punkt_",
)
