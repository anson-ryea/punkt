package com.an5on.config

import com.an5on.system.SystemUtils
import kotlinx.serialization.json.Json

/**
 * Provides access to the active configuration loaded from the configuration file.
 *
 * @author Anson Ng &lt;hej@an5on.com&gt;
 * @since 0.1.0
 */
object ActiveConfiguration {
    /**
     * The loaded configuration instance.
     */
    val configuration = loadJsonConfig()

    private fun loadJsonConfig(): Configuration {
        val configContent = SystemUtils.configPath.toFile().takeIf { it.exists() }?.readText() ?: "{}"
        return Json.decodeFromString<Configuration>(configContent)
    }
}