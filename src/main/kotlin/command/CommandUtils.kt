package com.an5on.command

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.Verbosity
import kotlin.enums.enumEntries

object CommandUtils {
    const val ECHO_CONTENT_INDENTATION = "    " // 4 spaces
    fun determineVerbosity(verbosityOption: Verbosity?) = verbosityOption ?: configuration.global.verbosity

    inline fun <reified T : Enum<T>> Enum.Companion.toChoices() =
        enumEntries<T>()
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray()

    inline fun <reified T : Enum<T>> Enum.Companion.enumEntryOf(choice: String) =
        enumValueOf<T>(choice.uppercase().replace("-", "_"))

    fun String.indented() = prependIndent(ECHO_CONTENT_INDENTATION)
}