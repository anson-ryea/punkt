package com.an5on.system

import java.nio.file.Path
import kotlin.io.path.Path

/**
 * A utility object for accessing system-related information and paths.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object SystemUtils {
    val osType = when {
        System.getProperty("os.name").lowercase().startsWith("windows") -> OsType.WINDOWS
        System.getProperty("os.name").lowercase().startsWith("mac") -> OsType.DARWIN
        else -> OsType.LINUX
    }

    val username: String = System.getProperty("user.name")
    val environmentVariables = System.getenv().toMutableMap()
    val shell: String = System.getenv("SHELL") ?: when (osType) {
        OsType.WINDOWS -> "powershell.exe"
        else -> "/bin/bash"
    }

    val homePath = Path(System.getProperty("user.home"))
    val workingPath = Path(System.getProperty("user.dir"))

    /**
     * Path to Punkt configuration file.
     *
     * @since 0.1.0
     */
    val configPath: Path = when (osType) {
       OsType.WINDOWS -> System.getenv("APPDATA")?.let { Path(it) } ?: homePath.resolve("AppData").resolve("Roaming")
        OsType.DARWIN -> homePath.resolve("Library/Application Support")
        OsType.LINUX -> System.getenv("XDG_CONFIG_HOME")?.let { Path(it) } ?: homePath.resolve(".config")
    }.resolve("punkt/punkt.json")
    /**
     * Path to Punkt logs.
     *
     * @since 0.1.0
     */
    val logPath: Path = when (osType) {
        OsType.WINDOWS -> System.getenv("LOCALAPPDATA")?.let { Path(it) }
            ?: homePath.resolve("\\AppData\\Local\\punkt\\logs")

        OsType.DARWIN -> homePath.resolve("Library/Logs/punkt")
        OsType.LINUX -> System.getenv("XDG_STATE_HOME")?.let { Path(it) }?.resolve("punkt/logs")
            ?: homePath.resolve("/.local/state/punkt/logs")
    }
    val sshIdentitiesPath: Path = when (osType) {
        OsType.WINDOWS -> homePath.resolve(".ssh")
        else -> homePath.resolve(".ssh")
    }
}