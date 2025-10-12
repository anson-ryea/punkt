package com.an5on.config

import com.an5on.system.SystemUtils
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.ExperimentalHoplite
import com.sksamuel.hoplite.addResourceSource
import kotlin.io.path.pathString

object ActiveConfiguration {
    @OptIn(ExperimentalHoplite::class)
    val config = ConfigLoaderBuilder
        .default()
        .withExplicitSealedTypes()
        .addResourceSource(SystemUtils.configPath.pathString, optional = true)
        .build()
        .loadConfigOrThrow<Configuration>()
}