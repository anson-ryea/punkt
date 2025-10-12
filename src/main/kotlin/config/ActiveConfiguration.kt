package com.an5on.config

import com.an5on.system.SystemUtils
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import kotlin.io.path.pathString

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
    @OptIn(ExperimentalHoplite::class)
    val config = ConfigLoaderBuilder
        .default()
        .withExplicitSealedTypes()
        .addResourceSource(SystemUtils.configPath.pathString, optional = true)
        .build()
        .loadConfigOrThrow<Configuration>()
}