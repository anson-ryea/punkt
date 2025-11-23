package com.an5on.hub.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
import kotlinx.serialization.Serializable
import java.nio.file.Path

/**
 * Configuration for the hub client, including server endpoint and token storage path.
 *
 * @property serverUrl Base URL of the hub backend server.
 * @property tokenPath File system path to the persisted authentication token.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
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