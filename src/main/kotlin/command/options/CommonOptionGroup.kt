package com.an5on.command.options

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

/**
 * Common option group for commands that mainly focus on file operations.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class CommonOptionGroup : OptionGroup("Common Options") {
    /**
     * Whether to process directories recursively.
     */
    val recursive by option("-r", "--recursive", help = "Operate on directories recursively").flag("--no-recursive", default = true)

    /**
     * Regex pattern for including file paths.
     */
    val include by option("-i", "--include", help = "Include file paths matching the regex pattern").convert { Regex(it) }
        .defaultLazy { Regex(".*") } // Matches everything if include is null

    /**
     * Regex pattern for excluding file paths.
     */
    val exclude by option("-x", "--exclude", help = "Exclude file paths matching the regex pattern").convert { Regex(it) }
        .defaultLazy { Regex("$^") } // Matches nothing if exclude is null
}