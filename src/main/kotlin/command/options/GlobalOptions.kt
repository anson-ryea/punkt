package com.an5on.command.options

import com.an5on.command.CommandUtils.enumEntryOf
import com.an5on.command.CommandUtils.toChoices
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.*
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.optionalValue
import com.github.ajalt.clikt.parameters.types.choice

class GlobalOptions : OptionGroup("Global Options") {
    val verbosity by option(
        "-v", "--verbosity"
    )
        .choice(
            *Enum.toChoices<Verbosity>()
        )
        .convert {
            Enum.enumEntryOf<Verbosity>(it)
        }
        .optionalValue(Verbosity.FULL, false)
        .default(configuration.global.verbosity)

    val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying the list of managed dotfiles."
    )
        .choice(
            *Enum.toChoices<PathStyle>()
        )
        .convert { Enum.enumEntryOf<PathStyle>(it) }
        .default(configuration.global.pathStyle)

    val interactivity by option(
        "-y",
        "--interactivity"
    )
        .choice(
            *Enum.toChoices<Interactivity>()
        )
        .convert { Enum.enumEntryOf<Interactivity>(it) }
        .optionalValue(Interactivity.NEVER, false)
        .default(configuration.global.interactivity)

    val useBundledGit by option()
        .choice(
            *Enum.toChoices<BooleanWithAuto>()
        )
        .convert {
            Enum.enumEntryOf<BooleanWithAuto>(it)
        }
        .optionalValue(BooleanWithAuto.TRUE, false)
        .default(configuration.git.useBundledGit)

    val gitOnLocalChange by option()
        .choice(
            *Enum.toChoices<GitOnLocalChange>()
        )
        .convert { Enum.enumEntryOf<GitOnLocalChange>(it) }
        .default(configuration.git.gitOnLocalChange)

    val gitCommitMessage by option()
        .default(configuration.git.commitMessage)
}