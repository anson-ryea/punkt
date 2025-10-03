package com.an5on.config

import kotlinx.serialization.Serializable
import kotlin.io.path.Path

/**
 * Configuration class containing application-wide settings.
 *
 * @property localDirAbsPathname The absolute path to the local directory where Punkt stores its data.
 * By default, it is `~/.local/share/punkt` in UNIX environments.
 * @author Anson Ng
 */
@Serializable
data class Configuration (
    val homeDirAbsPathname: String = System.getProperty("user.home"),
    val localDirAbsPathname: String = "$homeDirAbsPathname/.local/share/punkt",
    val trackedDbAbsPathname: String = "$homeDirAbsPathname/Library/Application Support/punkt/tracked",
    val dotReplacementString: String = "punkt_",
) {
    val homeDirAbsPath = Path(homeDirAbsPathname)
    val localDirAbsPath = Path(localDirAbsPathname)
    val trackedDbAbsPath = Path(trackedDbAbsPathname)

    companion object {
        val active = Configuration()
    }
}
