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
open class Configuration (
    val localDirAbsPathname: String = getDefaultLocalDirAbsPathname(),
    val trackedDbAbsPathname: String = getDefaultTrackedDbAbsPathname(),
    val sshPathname: String = "${homeDirAbsPathname}/.ssh",
    val sshPrivateKeyPathname: String? = null,
    val dotReplacementString: String = "punkt_",
) {
    val homeDirAbsPath = Path(homeDirAbsPathname)
    val localDirAbsPath = Path(localDirAbsPathname)
    val trackedDbAbsPath = Path(trackedDbAbsPathname)

    companion object {
        val homeDirAbsPathname: String = System.getProperty("user.home")
        val osName = System.getProperty("os.name").lowercase()

        private fun getDefaultLocalDirAbsPathname() = "${homeDirAbsPathname}/.local/share/punkt"
        private fun getDefaultTrackedDbAbsPathname() = when {
            osName.contains("windows") -> "${homeDirAbsPathname}\\AppData\\Roaming\\Punkt\\tracked"
            osName.contains("mac") -> "${homeDirAbsPathname}/Library/Application Support/punkt/tracked"
            else -> "${homeDirAbsPathname}/.config/punkt/tracked"
        }
    }
}
