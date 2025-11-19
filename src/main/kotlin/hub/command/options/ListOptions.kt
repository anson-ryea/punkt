package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.flag
import com.github.ajalt.clikt.parameters.options.option

class ListOptions : PunktOptionGroup() {
    val mine by option().flag()
}