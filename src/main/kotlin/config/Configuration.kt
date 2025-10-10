package com.an5on.config

import com.an5on.system.OsType
import com.an5on.system.SystemUtils
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
    val localDirAbsPathname: String = defaultLocalDirAbsPathname,
    val trackedDbAbsPathname: String = defaultTrackedDbAbsPathname,
    val sshPathname: String = "${homeDirAbsPathname}/.ssh",
    val sshPrivateKeyPathname: String? = null,
    val dotReplacementString: String = "punkt_",
) {
    val homeDirAbsPath = Path(homeDirAbsPathname)
    val localDirAbsPath = Path(localDirAbsPathname)
    val trackedDbAbsPath = Path(trackedDbAbsPathname)

    companion object {
        val homeDirAbsPathname: String = System.getProperty("user.home")

        val defaultLocalDirAbsPathname = "${homeDirAbsPathname}/.local/share/punkt"
        val defaultTrackedDbAbsPathname = when (SystemUtils.osType) {
            OsType.WINDOWS -> "${homeDirAbsPathname}\\AppData\\Local\\punkt\\tracked"
            OsType.DARWIN -> "${homeDirAbsPathname}/Library/Application Support/punkt/tracked"
            OsType.LINUX -> "${homeDirAbsPathname}/.config/punkt/tracked"
        }

        val defaultLogDirAbsPathname = when (SystemUtils.osType) {
            OsType.WINDOWS ->
                "${homeDirAbsPathname}\\AppData\\Local\\punkt\\logs"
            OsType.DARWIN ->
                "${homeDirAbsPathname}/Library/Logs/punkt"
            OsType.LINUX ->
                "${homeDirAbsPathname}/.config/punkt/logs"
        }
    }
}
