package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt
import com.github.ajalt.clikt.parameters.options.validate

/**
 * Options for the `login` command.
 *
 * Provides the credentials required to authenticate with Punkt Hub.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class LoginOptions : PunktOptionGroup() {
    val email by option("--email").prompt("email").validate {
        it.isNotBlank()
    }
    val password by option("--password").prompt("password", hideInput = true).validate {
        it.isNotBlank()
    }
}