package com.an5on.command.options

import com.an5on.operation.PathStyle
import com.an5on.type.BooleanWithAuto
import com.an5on.type.VerbosityType
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice

class GlobalOptions : OptionGroup("Global Options") {
    val useBundledGit by option()
        .choice(
            *BooleanWithAuto.entries
                .map { it.name.lowercase().replace("_", "-") }
                .toTypedArray(),
        )
        .convert {
            BooleanWithAuto.valueOf(
                it.uppercase().replace("-", "_")
            )
        }

    val verbosity by option()
        .choice(
            *VerbosityType.entries
                .map { it.name.lowercase().replace("_", "-") }
                .toTypedArray()
        )
        .convert {
            VerbosityType.valueOf(
                it.uppercase().replace("-", "_")
            )
        }

    val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying the list of managed dotfiles. Options are 'absolute' or 'relative' to the home directory."
    )
        .choice(
            *PathStyle.entries
                .map { it.name.lowercase().replace("_", "-") }
                .toTypedArray(),
        )
        .convert { PathStyle.valueOf(it.uppercase().replace("-", "_")) }
        .default(PathStyle.ABSOLUTE)
}