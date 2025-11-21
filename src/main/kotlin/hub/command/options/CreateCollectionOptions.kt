package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt

class CreateCollectionOptions : PunktOptionGroup() {
    val name: String by option().prompt("name").check({
        "Name must not be blank."
    }, {
        it.isNotBlank()
    })
    val description: String by option().prompt("description", default = "")
    val private: Boolean by option().flag()
}