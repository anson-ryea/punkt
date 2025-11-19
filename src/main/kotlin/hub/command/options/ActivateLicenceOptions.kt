package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate

class ActivateLicenceOptions : PunktOptionGroup() {
    val key by option().prompt("licence key").validate {
        it.isNotBlank()
    }
}