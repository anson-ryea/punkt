package com.an5on.command.options

import com.an5on.command.CommandUtils.enumEntryOf
import com.an5on.command.CommandUtils.toChoices
import com.an5on.operation.PathStyle
import com.an5on.type.BooleanWithAuto
import com.an5on.type.GitOnLocalChangeType
import com.an5on.type.VerbosityType
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.*
import com.github.ajalt.clikt.parameters.types.choice

class GlobalOptions : OptionGroup("Global Options") {
    val useBundledGit by option()
        .choice(
            *Enum.toChoices<BooleanWithAuto>()
        )
        .convert {
            Enum.enumEntryOf<BooleanWithAuto>(it)
        }

    val verbosity by option(
        "-v", "--verbosity"
    )
        .choice(
            *Enum.toChoices<VerbosityType>()
        )
        .convert {
            Enum.enumEntryOf<VerbosityType>(it)
        }
        .optionalValue(VerbosityType.VERBOSE)

    val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying the list of managed dotfiles. Options are 'absolute' or 'relative' to the home directory."
    )
        .choice(
            *Enum.toChoices<PathStyle>()
        )
        .convert { Enum.enumEntryOf<PathStyle>(it) }
        .default(PathStyle.ABSOLUTE)

    val prompt by option().flag()

    val gitOnLocalChange by option()
        .choice(
            *Enum.toChoices<GitOnLocalChangeType>()
        )
        .convert { Enum.enumEntryOf<GitOnLocalChangeType>(it) }
}