package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.error.PunktError
import com.an5on.file.PunktIgnore
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal

class IgnoredOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal,
): Operable {
    override fun operate(): Either<PunktError, Unit> = either {
        val message = PunktIgnore.ignorePatterns.joinToString(separator = "\n")

        echos.echoWithVerbosity(
            message,
            message.isNotBlank(),
            false,
            globalOptions.verbosity,
            Verbosity.QUIET
        )
    }
}