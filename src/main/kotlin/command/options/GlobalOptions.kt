package com.an5on.command.options

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.type.*
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.optionalValue
import com.github.ajalt.clikt.parameters.types.choice

/**
 * A group of global command-line options that can be applied to any `punkt` command.
 *
 * This class encapsulates settings that affect the overall behaviour of the application, such as output verbosity,
 * path display styles, and interaction modes. These options are configured to fall back to default values defined
 * in the application's configuration if not explicitly provided on the command line.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class GlobalOptions : PunktOptionGroup("Global Options") {
    /**
     * Controls the level of detail in the command's output.
     *
     * The verbosity can be set to `QUIET`, `NORMAL`, or `FULL`. Using `-v` or `--verbosity` without a value
     * defaults to `FULL`. The application's default is loaded from the configuration.
     */
    val verbosity by option(
        "-v", "--verbosity",
        help = "Set the level of detail in the command's output"
    )
        .choice(
            *Enum.toChoices<Verbosity>()
        )
        .convert {
            Enum.enumEntryOf<Verbosity>(it)
        }
        .optionalValue(Verbosity.FULL, false)
        .default(configuration.global.verbosity)

    /**
     * Determines how file paths are displayed in command output.
     *
     * This can be set to `ABSOLUTE`, `RELATIVE`, `LOCAL_ABSOLUTE`, or `LOCAL_RELATIVE`.
     * The default style is loaded from the configuration.
     */
    val pathStyle by option(
        "-p", "--path-style",
        help = "Set the path style for displaying paths"
    )
        .choice(
            *Enum.toChoices<PathStyle>()
        )
        .convert { Enum.enumEntryOf<PathStyle>(it) }
        .default(configuration.global.pathStyle)

    /**
     * Controls whether the application should prompt for user confirmation during potentially destructive operations.
     *
     * It can be set to `ALWAYS`, `NEVER`. Using `-y` or `--interactivity` without a value defaults
     * to `NEVER`. The default behaviour is loaded from the configuration.
     */
    val interactivity by option(
        "-y",
        "--interactivity",
        help = "Whether the application should prompt for user confirmation during potentially destructive operations"
    )
        .choice(
            *Enum.toChoices<Interactivity>()
        )
        .convert { Enum.enumEntryOf<Interactivity>(it) }
        .optionalValue(Interactivity.NEVER, false)
        .default(configuration.global.interactivity)

    /**
     * Determines whether to use the bundled Git or the system-installed Git for Git Operations.
     *
     * This can be `TRUE`, `FALSE`, or `AUTO`. `AUTO` will attempt to use the system's Git and fall back to the
     * bundled version if not found. Using the option flag without a value defaults to `TRUE`. The default is
     * loaded from the configuration.
     */
    val useBundledGit by option(help = "Whether to use the bundled Git for Git Operations")
        .choice(
            *Enum.toChoices<BooleanWithAuto>()
        )
        .convert {
            Enum.enumEntryOf<BooleanWithAuto>(it)
        }
        .optionalValue(BooleanWithAuto.TRUE, false)
        .default(configuration.git.useBundledGit)

    /**
     * Defines the automatic Git action to perform when local file changes are detected.
     *
     * This can be set to `ADD_COMMIT_PUSH`, `COMMIT_PUSH`, `ADD_COMMIT`, `COMMIT`, `ADD`, or `NONE`. The default action is loaded from the configuration.
     */
    val gitOnLocalChange by option(help = "Automatically perform Git operations to perform on local state changes")
        .choice(
            *Enum.toChoices<GitOnLocalChange>()
        )
        .convert { Enum.enumEntryOf<GitOnLocalChange>(it) }
        .default(configuration.git.gitOnLocalChange)

    /**
     * The default commit message to use for automatic Git commits.
     *
     * This message is used when `gitOnLocalChange` contains `COMMIT`. The default message is loaded from the
     * configuration.
     */
    val gitCommitMessage by option(help = "Set the commit message for automatic Git commits")
        .default(configuration.git.commitMessage)
}