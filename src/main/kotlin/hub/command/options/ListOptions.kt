package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

/**
 * Options for the `list` command.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class ListOptions : PunktOptionGroup() {
    val mine by option(help = "Include items belong to you only").flag()
}