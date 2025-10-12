package com.an5on.command

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class CommonOptionGroup : OptionGroup("Common Options:") {
    val recursive by option("-r", "--recursive", help = "Diff directories recursively").flag(default = true)
    val include by option("-i", "--include", help = "Include files matching the regex pattern").convert { Regex(it) }
        .defaultLazy { Regex(".*") } // Matches everything if include is null
    val exclude by option("-x", "--exclude", help = "Exclude files matching the regex pattern").convert { Regex(it) }
        .defaultLazy { Regex("$^") } // Matches nothing if exclude is null
}