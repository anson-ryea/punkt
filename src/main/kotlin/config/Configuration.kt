package com.an5on.config

import kotlinx.serialization.Serializable

@Serializable
data class Configuration(
    val global: GlobalConfiguration = GlobalConfiguration(),
//    val command: CommandConfiguration,
    val git: GitConfiguration = GitConfiguration(),
)
