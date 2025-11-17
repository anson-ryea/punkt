package com.an5on.hub.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import kotlinx.serialization.Serializable
import java.nio.file.Path

@Serializable
data class HubConfiguration (
    val serverUrl: String = "https://dot-backend-85b8.onrender.com",
    val tokenPath: Path = SystemUtils.homePath.resolve(
        when (SystemUtils.osType) {
            OsType.WINDOWS -> "AppData\\Local\\punkt\\hub_token.json"
            OsType.DARWIN -> "Library/Application Support/punkt/hub_token.json"
            OsType.LINUX -> ".config/punkt/hub_token.json"
        }
    ),
)