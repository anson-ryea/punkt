package com.an5on.config

import kotlinx.serialization.Serializable

/**
 * Represents the main configuration for the Punkt application.
 *
 * This data class holds all the configuration settings, which are deserialized from a JSON file. It includes global
 * settings, as well as configurations for specific features like Git integration.
 *
 * @property global The [GlobalConfiguration] containing settings that apply across the application.
 * @property git The [GitConfiguration] for Git-related operations and settings.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
@Serializable
data class Configuration(
    val global: GlobalConfiguration = GlobalConfiguration(),
//    val command: CommandConfiguration,
    val git: GitConfiguration = GitConfiguration(),
)
