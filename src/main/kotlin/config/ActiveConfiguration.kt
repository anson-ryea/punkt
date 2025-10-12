package com.an5on.config

import com.an5on.system.SystemUtils
import com.sksamuel.hoplite.ConfigLoaderBuilder
import com.sksamuel.hoplite.addResourceSource
import kotlin.io.path.pathString

object ActiveConfiguration {
    val config = ConfigLoaderBuilder.default()
        .addResourceSource(SystemUtils.configPath.pathString)
        .build()
        .loadConfigOrThrow<Configuration>()
}