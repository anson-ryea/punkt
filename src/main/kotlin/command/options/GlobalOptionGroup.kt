package com.an5on.command.options

import com.an5on.type.BooleanWithAutoAndDefault
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

class GlobalOptionGroup : OptionGroup() {
    val useBundledGit by option()
        .choice(
            *BooleanWithAutoAndDefault.entries
                .map { it.name.lowercase().replace("_", "-") }
                .toTypedArray(),
        )
        .convert {
            BooleanWithAutoAndDefault.valueOf(
                it.uppercase().replace("-", "_")
            )
        }
        .default(BooleanWithAutoAndDefault.AUTO)
}