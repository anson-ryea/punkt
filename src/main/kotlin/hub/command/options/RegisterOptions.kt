package com.an5on.hub.command.options

import com.an5on.command.options.PunktOptionGroup
import com.github.ajalt.clikt.parameters.options.check
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.prompt

/**
 * Options for the `register` command.
 *
 * Captures the details required to create a new Punkt Hub account.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class RegisterOptions : PunktOptionGroup() {
    val username: String by option().prompt("username").check({
        "Username must not be blank."
    }, {
        it.isNotBlank()
    })
    val email: String by option().prompt("email").check({
        "Email must not be blank."
    }, {
        it.isNotBlank()
    })
    val password: String by option().prompt("password", requireConfirmation = true).check({
        "Password must not be blank."
    }, {
        it.isNotBlank()
    })
}