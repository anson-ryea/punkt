package com.an5on.config

import com.an5on.system.SystemUtils
import kotlinx.serialization.json.Json

/**
 * Provides centralised access to the application's active configuration.
 *
 * This object is responsible for loading the configuration from a JSON file upon initialisation and making it
 * available globally as a singleton instance. If the configuration file does not exist, it loads a default,
 * empty configuration.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
object ActiveConfiguration {
    /**
     * The globally accessible [Configuration] instance, loaded from the JSON configuration file.
     */
    val configuration = loadJsonConfig()

    /**
     * Loads the application configuration from the default configuration path.
     *
     * This function reads the content of the JSON configuration file located at [SystemUtils.configPath]. If the file
     * exists, its content is parsed into a [Configuration] object. If the file is not found, an empty JSON object
     * string (`{}`) is used, resulting in a default [Configuration] instance.
     *
     * @return The loaded [Configuration] object.
     */
    private fun loadJsonConfig(): Configuration {
        val configContent = SystemUtils.configPath.toFile().takeIf { it.exists() }?.readText() ?: "{}"
        return Json.decodeFromString<Configuration>(configContent)
    }
}