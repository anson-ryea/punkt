package com.an5on.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import com.an5on.type.Interactivity
import com.an5on.type.PathStyle
import com.an5on.type.Verbosity
import kotlinx.serialization.Serializable
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
@Serializable
data class GlobalConfiguration(
    val localStatePath: Path = SystemUtils.homePath.resolve(
//        if (SystemUtils.osType == OsType.WINDOWS) {
//            "AppData\\Roaming\\punkt"
//        } else {
            ".local/share/punkt"
//        }
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
    val verbosity: Verbosity = Verbosity.NORMAL,
    val pathStyle: PathStyle = PathStyle.ABSOLUTE,
    val interactivity: Interactivity = Interactivity.ALWAYS,
    val ignoredActiveFilesForDarwin: Set<String> = setOf(
        ".DS_Store",
        ".localized",
        ".AppleDouble",
        "__MACOSX",
        ".LSOverride",
        "._*",
        "Icon",
        ".DocumentRevisions-V100",
        ".fseventsd",
        ".Spotlight-V100",
        ".TemporaryItems",
        ".Trashes",
        ".VolumeIcon.icns",
        ".com.apple.timemachine.donotpresent",
        ".AppleDB",
        ".AppleDesktop",
        "Network Trash Folder",
        "Temporary Items",
        ".apdisk",
    ),
    val ignoredActiveFilesForWindows: Set<String> = setOf(
        "Thumbs.db",
        "Thumbs.db:encryptable",
        "ehthumbs.db",
        "ehthumbs_vista.db",
        "*.stackdump",
        "[Dd]esktop.ini",
        $$"$RECYCLE.BIN/**",
        "*.cab",
        "*.msi",
        "*.msix",
        "*.msm",
        "*.msp",
        "*.lnk"
    ),
    val ignoredActiveFilesForLinux: Set<String> = setOf(
        ".*.swp",
        ".*.swo",
        ".*.swx",
        ".directory",
        ".Trash-*/**",
        ".cache/**",
        ".local/share/Trash/**",
        "*~",
        ".fuse_hidden*",
        ".directory",
        ".nfs*",
        "nohup.out"
    ),
    val ignoredLocalFiles: Set<String> = setOf(
        ".*",
        ".*/**"
    )
)
