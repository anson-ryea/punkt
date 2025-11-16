package com.an5on.command.options

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

/**
 * A group of command-line options specific to the `sync` command.
 *
 * This class encapsulates options that control the behaviour of the synchronization process.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class SyncOptions : PunktOptionGroup() {
    /**
     * A flag to determine whether empty folders should be preserved during synchronization.
     *
     * When this flag is present (`--keep-empty-folders`), any empty directories encountered in the source
     * will be created in the destination. By default, empty directories are not copied.
     */
    val keepEmptyFolders: Boolean by option(
        help = "Keep empty folders during synchronization"
    ).flag()
}