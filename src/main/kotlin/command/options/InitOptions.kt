package com.an5on.command.options

import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

/**
 * A group of command-line options specific to the `init` command.
 *
 * This class encapsulates options that control how a new `punkt` repository is initialised, particularly when
 * cloning from a remote source. It allows specifying the connection protocol (SSH), the target branch, and the
 * clone depth.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class InitOptions : PunktOptionGroup() {
    /**
     * A flag to indicate that the SSH protocol should be used for cloning the remote repository.
     *
     * If this flag is present (`--ssh`), the command will attempt to use SSH credentials. Otherwise, it defaults
     * to HTTPS.
     */
    val ssh: Boolean by option(
        help = "Use SSH for cloning"
    ).flag()

    /**
     * The name of the branch to clone from the remote repository.
     *
     * If this option is not provided, the remote repository's default branch will be checked out.
     *
     * ### Example
     * To clone the `develop` branch: `--branch develop`
     */
    val branch: String? by option(
        names = arrayOf("-b", "--branch"),
        help = "Set the branch of the remote Punkt repository to clone"
    )

    /**
     * Creates a shallow clone with a history truncated to the specified number of commits.
     *
     * A shallow clone downloads only a limited history, which can be significantly faster and use less disk space.
     * If this option is not provided, a full clone with the entire history is performed.
     *
     * ### Example
     * To clone only the latest commit: `--depth 1`
     */
    val depth: Int? by option(
        names = arrayOf("-d", "--depth"),
        help = "Clone the remote Punkt repository shallowly with the specified depth"
    ).int()
}