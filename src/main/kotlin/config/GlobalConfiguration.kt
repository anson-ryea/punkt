package com.an5on.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import com.an5on.type.Interactivity
import com.an5on.type.PathStyle
import com.an5on.type.Verbosity
import kotlinx.serialization.Serializable
import java.nio.file.Path

/**
 * Represents the global configuration settings for the Punkt application.
 *
 * This data class holds settings that apply across the entire application, such as default paths, verbosity levels,
 * and patterns for ignored files. These settings are deserialized from a JSON configuration file.
 *
 * @property localStatePath The path to the local state directory where Punkt stores cloned dotfiles.
 * @property activeStatePath The path to the active state directory, which is typically the user's home directory.
 * @property trackerPath The path to the tracker database directory where tracked file states are stored.
 * @property sshPath The path to the SSH directory, used for SSH key management.
 * @property dotReplacementPrefix The prefix used to replace leading dots in file names when storing them locally.
 * @property verbosity The default level of detail for command output.
 * @property pathStyle The preferred style for displaying file paths (e.g., absolute or relative).
 * @property interactivity The default mode for user interaction (e.g., always prompt or proceed automatically).
 * @property ignoredActiveFilesForDarwin A set of file patterns to ignore on macOS systems.
 * @property ignoredActiveFilesForWindows A set of file patterns to ignore on Windows systems.
 * @property ignoredActiveFilesForLinux A set of file patterns to ignore on Linux systems.
 * @property ignoredLocalFiles A set of file patterns to ignore within the local state directory.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
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
        "**/.DS_Store",
        ".localized",
        "**/.localized",
        ".AppleDouble",
        "**/.AppleDouble",
        "__MACOSX",
        "**/__MACOSX",
        ".LSOverride",
        "**/.LSOverride",
        "._*",
        "**/._*",
        "Icon",
        "**/Icon",
        ".DocumentRevisions-V100",
        "**/.DocumentRevisions-V100",
        ".fseventsd",
        "**/.fseventsd",
        ".Spotlight-V100",
        "**/.Spotlight-V100",
        ".TemporaryItems",
        "**/.TemporaryItems",
        ".Trashes",
        "**/.Trashes",
        ".VolumeIcon.icns",
        "**/.VolumeIcon.icns",
        ".com.apple.timemachine.donotpresent",
        "**/.com.apple.timemachine.donotpresent",
        ".AppleDB",
        "**/.AppleDB",
        ".AppleDesktop",
        "**/.AppleDesktop",
        "Network Trash Folder",
        "**/Network Trash Folder",
        "Temporary Items",
        "**/Temporary Items",
        ".apdisk",
        "**/.apdisk",
    ),
    val ignoredActiveFilesForWindows: Set<String> = setOf(
        "Thumbs.db",
        "**/Thumbs.db",
        "Thumbs.db:encryptable",
        "**/Thumbs.db:encryptable",
        "ehthumbs.db",
        "**/ehthumbs.db",
        "ehthumbs_vista.db",
        "**/ehthumbs_vista.db",
        "*.stackdump",
        "**/*.stackdump",
        "[Dd]esktop.ini",
        "**/[Dd]esktop.ini",
        $$"$RECYCLE.BIN/**",
        $$"**/$RECYCLE.BIN/**",
        "*.cab",
        "**/*.cab",
        "*.msi",
        "**/*.msi",
        "*.msix",
        "**/*.msix",
        "*.msm",
        "**/*.msm",
        "*.msp",
        "**/*.msp",
        "*.lnk",
        "**/*.lnk"
    ),
    val ignoredActiveFilesForLinux: Set<String> = setOf(
        ".*.swp",
        "**/.*.swp",
        ".*.swo",
        "**/.*.swo",
        ".*.swx",
        "**/.*.swx",
        ".directory",
        "**/.directory",
        ".Trash-*/**",
        "**/.Trash-*/**",
        ".cache/**",
        "**/.cache/**",
        ".local/share/Trash/**",
        "**/.local/share/Trash/**",
        "*~",
        "**/*~",
        ".fuse_hidden*",
        "**/.fuse_hidden*",
        ".nfs*",
        "**/.nfs*",
        "nohup.out",
        "**/nohup.out"
    ),
    val ignoredLocalFiles: Set<String> = setOf(
        "**/.**",
        ".**"
    )
)
