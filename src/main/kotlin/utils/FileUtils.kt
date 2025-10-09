package com.an5on.utils

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration
import com.an5on.error.PunktError
import com.an5on.states.active.ActiveState.toActivePath
import com.an5on.states.local.LocalState.toLocalPath
import org.apache.commons.codec.digest.Blake3
import org.apache.commons.io.filefilter.IOFileFilter
import java.io.File
import kotlin.io.path.pathString

object FileUtils {
    fun replaceTildeWithAbsPathname(pathname: String): String {
        return if (pathname.startsWith("~")) {
            pathname.replaceFirst("~", ActiveConfiguration.homeDirAbsPath.pathString)
        } else {
            pathname
        }
    }

    fun getBlake3HashHexString(file: File) = Blake3.hash(file.readBytes()).toHexString()

    fun IOFileFilter.onActive(): Either<PunktError, IOFileFilter> = either {
        object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                val activeFile = file?.toPath()?.toActivePath()?.bind()?.toFile()

                return if (activeFile != null && activeFile.exists()) {
                    this@onActive.accept(activeFile)
                } else {
                    false
                }
            }

            override fun accept(dir: File?, name: String?): Boolean {
                val activeFile = if (dir != null && name != null) {
                    File(dir, name).toPath().toActivePath().bind().toFile()
                } else {
                    null
                }

                return if (activeFile != null && activeFile.exists()) {
                    this@onActive.accept(activeFile)
                } else {
                    false
                }
            }
        }
    }

    fun IOFileFilter.onLocal(): Either<PunktError, IOFileFilter> = either {
        object : IOFileFilter {
            override fun accept(file: File?): Boolean {
                val localFile = file?.toPath()?.toLocalPath()?.bind()?.toFile()
                return if (localFile != null && localFile.exists()) {
                    this@onLocal.accept(localFile)
                } else {
                    false
                }
            }

            override fun accept(dir: File?, name: String?): Boolean {
                val localFile = if (dir != null && name != null) {
                    File(dir, name).toPath().toLocalPath().bind().toFile()
                } else {
                    null
                }

                return if (localFile != null && localFile.exists()) {
                    this@onLocal.accept(localFile)
                } else {
                    false
                }
            }
        }
    }
}