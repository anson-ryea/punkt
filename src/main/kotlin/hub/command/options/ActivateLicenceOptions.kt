package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate

/**
 * Options for the `activate-licence` command.
 *
 * Provides the licence key used to activate Punkt Hub.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class ActivateLicenceOptions : PunktOptionGroup() {
    val key by option().prompt("licence key").validate {
        it.isNotBlank()
    }
}