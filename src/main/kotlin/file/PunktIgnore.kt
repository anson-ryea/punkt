package com.an5on.file

import com.an5on.config.ActiveConfiguration.configuration
import java.nio.file.Path
import kotlin.io.path.Path

object PunktIgnore : Ignore {
    val ignoreFilePath = Path("${configuration.global.localStatePath}/.punktignore")
    override val ignorePatterns
        get() = parse(ignoreFilePath)

    private fun parse(ignoreFilePath: Path): Set<String> {
        val ignoreFile = ignoreFilePath.toFile()

        if (!ignoreFile.exists()) {
            return setOf()
        }

        val lines = ignoreFile
            .readLines()
            .map { it.stripComment() }
            .filterNot { it.isBlank() }
            .toSet()

        return lines
    }

    private fun String.stripComment(): String {
        val commentIndex = indexOf('#')
        return if (commentIndex >= 0) {
            take(commentIndex).trim()
        } else {
            trim()
        }
    }
}