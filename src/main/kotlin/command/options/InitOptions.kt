package com.an5on.command.options

import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.int

/**
 * @property ssh A flag indicating whether to use SSH for cloning the remote Punkt repository.
 * @property branch The branch of the remote Punkt repository to clone. If not provided, the default branch is cloned.
 * @property depth The depth for a shallow clone of the remote Punkt repository. If not provided, a full clone is performed.
 */
class InitOptions : OptionGroup() {
    val ssh: Boolean by option(
        help = "Use SSH for cloning"
    ).flag()
    val branch: String? by option(
        names = arrayOf("-b", "--branch"),
        help = "Set the branch of the remote Punkt repository to clone"
    )
    val depth: Int? by option(
        names = arrayOf("-d", "--depth"),
        help = "Clone the remote Punkt repository shallowly with the specified depth"
    ).int()
}