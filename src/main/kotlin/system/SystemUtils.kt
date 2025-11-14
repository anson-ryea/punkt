package com.an5on.system

import java.nio.file.Path
import kotlin.io.path.Path

object SystemUtils {
    val osType = when {
        System.getProperty("os.name").lowercase().startsWith("windows") -> OsType.WINDOWS
        System.getProperty("os.name").lowercase().startsWith("mac") -> OsType.DARWIN
        else -> OsType.LINUX
    }

    val workingPath = Path(System.getProperty("user.dir"))

    val homePath = Path(System.getProperty("user.home"))

    val environmentVariables = System.getenv().toMutableMap()

    val configPath: Path = when (osType) {
        OsType.WINDOWS -> System.getenv("APPDATA")?.let { Path(it) } ?: homePath.resolve("\\AppData\\Roaming")
        OsType.DARWIN -> homePath.resolve("Library/Application Support")
        OsType.LINUX -> System.getenv("XDG_CONFIG_HOME")?.let { Path(it) } ?: homePath.resolve("/.config")
    }.resolve("punkt/punkt.json")

    val logPath: Path = when (osType) {
        OsType.WINDOWS -> System.getenv("LOCALAPPDATA")?.let { Path(it) }
            ?: homePath.resolve("\\AppData\\Local\\punkt\\logs")

        OsType.DARWIN -> homePath.resolve("Library/Logs/punkt")
        OsType.LINUX -> System.getenv("XDG_STATE_HOME")?.let { Path(it) }?.resolve("punkt/logs")
            ?: homePath.resolve("/.local/state/punkt/logs")
    }

    val username: String = System.getProperty("user.name")
    val shell: String = System.getenv("SHELL") ?: when (osType) {
        OsType.WINDOWS -> "powershell.exe"
        else -> "/bin/bash"
    }

    val sshIdentitiesPath: Path = when (osType) {
        OsType.WINDOWS -> homePath.resolve(".ssh")
        else -> homePath.resolve(".ssh")
    }
}