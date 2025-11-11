package com.an5on.command.options

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class SyncOptions: OptionGroup() {
    val keepEmptyFolders: Boolean by option(
        help = "Keep empty folders during synchronization"
    ).flag()
}