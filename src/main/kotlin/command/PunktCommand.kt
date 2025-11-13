package com.an5on.command

import com.an5on.command.CommandUtils.prependError
import com.an5on.command.CommandUtils.prependStage
import com.an5on.command.CommandUtils.prependSuccess
import com.an5on.command.CommandUtils.prependWaring
import com.an5on.error.PunktError
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import io.github.oshai.kotlinlogging.KotlinLogging

abstract class PunktCommand : CliktCommand() {
    private val logger = KotlinLogging.logger {}

    fun echoWithVerbosity(
        message: Any?,
        trailingNewLine: Boolean = true,
        err: Boolean = false,
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(message, trailingNewLine, err)
    }

    /**
     * Echoes a stage message with blue color and arrow.
     *
     * @param message the message to echo
     */
    fun echoStage(
        message: Any?,
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(
            TextStyles.bold(message.toString().prependStage())
        )
    }

    /**
     * Echoes a success message with green color and checkmark.
     *
     * @param message the message to echo, defaults to "Done!"
     */
    fun echoSuccess(
        message: Any? = "Done!",
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(message.toString().prependSuccess())
    }

    /**
     * Echoes a warning message with yellow color and warning symbol.
     *
     * @param message the message to echo
     */
    fun echoWarning(
        message: Any?,
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(
            TextColors.yellow(message.toString().prependWaring())
        )
    }

    fun echoError(
        message: Any?
    ) {
        echo(
            TextStyles.bold(TextColors.red(message.toString().prependError())),
            err = true
        )
    }

    protected val echos: Echos
        get() = Echos(
            ::echoWithVerbosity,
            ::echoStage,
            ::echoSuccess,
            ::echoWarning,
        )

    protected fun handleError(e: PunktError) {
        echoError(e.message)
        logger.error { e.message }
        throw ProgramResult(e.statusCode)
    }
}