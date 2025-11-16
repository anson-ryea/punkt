package com.an5on.command.options

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import kotlin.enums.enumEntries

abstract class PunktOptionGroup (
    name: String? = null,
    help: String? = null
) : OptionGroup(name, help) {
    inline fun <reified T : Enum<T>> Enum.Companion.toChoices() =
        enumEntries<T>()
            .map { it.name.lowercase().replace("_", "-") }
            .toTypedArray()

    inline fun <reified T : Enum<T>> Enum.Companion.enumEntryOf(choice: String) =
        enumValueOf<T>(choice.uppercase().replace("-", "_"))

}