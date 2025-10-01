package com.an5on.config

import kotlinx.serialization.Serializable

/**
 * Configuration object containing application-wide settings.
 *
 * @property localDirAbsPath The absolute path to the local directory where Punkt stores its data.
 * By default, it is `~/.local/share/punkt` in UNIX environments.
 * @author Anson Ng
 */
@Serializable
data object Configuration {
    val localDirAbsPath: String = "${System.getProperty("user.home")}/.local/share/punkt"
}
