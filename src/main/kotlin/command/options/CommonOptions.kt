package com.an5on.command.options

import com.github.ajalt.clikt.parameters.options.convert
import com.github.ajalt.clikt.parameters.options.defaultLazy
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

/**
 * A group of common command-line options for file-based operations, such as filtering and recursion.
 *
 * This class encapsulates options that are frequently used across various `punkt` commands that interact with
 * files and directories. It provides a consistent interface for:
 * - Traversing directories recursively (`-r`, `--recursive`).
 * - Including files that match a regular expression (`-i`, `--include`).
 * - Excluding files that match a regular expression (`-x`, `--exclude`).
 *
 * By default, recursion is enabled, all files are included (`.*`), and no files are excluded (`p^`, a pattern
 * that matches nothing).
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class CommonOptions : PunktOptionGroup("Common Options") {
    /**
     * A flag to control whether operations should apply to directories recursively.
     *
     * When enabled (`--recursive`, the default), commands will traverse into subdirectories. This can be disabled
     * with `--no-recursive`.
     */
    val recursive by option("-r", "--recursive", help = "Operate on directories recursively").flag(
        "--no-recursive",
        default = true
    )

    /**
     * A regular expression used to filter which files to include in an operation.
     *
     * Only files whose paths match this regex will be processed. The default value is `.*`, which matches all files.
     *
     * ### Example
     * To include only Markdown files: `--include ".*\\.md"`
     */
    val include by option(
        "-i",
        "--include",
        help = "Include file paths matching the regex pattern"
    ).convert { Regex(it) }
        .defaultLazy { Regex(".*") } // Matches everything if include is null

    /**
     * A regular expression used to filter which files to exclude from an operation.
     *
     * Files whose paths match this regex will be ignored, even if they also match the `--include` pattern.
     * The default value is `p^`, a pattern that is unlikely to match any path, effectively excluding nothing.
     *
     * ### Example
     * To exclude all hidden files (those starting with a dot): `--exclude ".*\\/\\..*"`
     */
    val exclude by option(
        "-x",
        "--exclude",
        help = "Exclude file paths matching the regex pattern"
    ).convert { Regex(it) }
        .defaultLazy { Regex("p^") } // Matches nothing if exclude is null
}