package com.an5on.command

import com.an5on.command.CommandUtils.determineVerbosity
import com.an5on.error.PunktError
import com.an5on.type.VerbosityType
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import io.github.oshai.kotlinlogging.KotlinLogging


fun CliktCommand.echoWithVerbosity(
    message: Any?,
    trailingNewLine: Boolean = true,
    err: Boolean = false,
    verbosityOption: VerbosityType?,
    minimumVerbosity: VerbosityType = VerbosityType.NORMAL
) {
    if (determineVerbosity(verbosityOption) < minimumVerbosity) return

    echo(message, trailingNewLine, err)
}

/**
 * Echoes a stage message with blue color and arrow.
 *
 * @param message the message to echo
 */
fun CliktCommand.echoStage(
    message: Any?,
    verbosityOption: VerbosityType?,
    minimumVerbosity: VerbosityType = VerbosityType.NORMAL
) {
    if (determineVerbosity(verbosityOption) < minimumVerbosity) return

    echo(
        TextStyles.bold((TextColors.cyan)("~~> ") + message)
    )
}

/**
 * Echoes a success message with green color and checkmark.
 *
 * @param message the message to echo, defaults to "Done!"
 */
fun CliktCommand.echoSuccess(
    message: Any? = "Done!",
    verbosityOption: VerbosityType?,
    minimumVerbosity: VerbosityType = VerbosityType.NORMAL
) {
    if (determineVerbosity(verbosityOption) < minimumVerbosity) return

    echo(
        TextColors.green(" :> ") + message
    )
}

/**
 * Echoes a warning message with yellow color and warning symbol.
 *
 * @param message the message to echo
 */
fun CliktCommand.echoWarning(
    message: Any?,
    verbosityOption: VerbosityType?,
    minimumVerbosity: VerbosityType = VerbosityType.NORMAL
) {
    if (determineVerbosity(verbosityOption) < minimumVerbosity) return

    echo(
        TextColors.yellow(" :| ") + message
    )
}

fun CliktCommand.echoError(
    message: Any?
) {
    echo(
        TextStyles.bold(TextColors.red(" :< $message")),
        err = true
    )
}

val CliktCommand.echos: Echos
    get() = Echos(
        echoWithVerbosity = { message, trailingNewLine, err, verbosityOption, minimumVerbosity ->
            echoWithVerbosity(message, trailingNewLine, err, verbosityOption, minimumVerbosity)
        },
        echoStage = { message, verbosityOption, minimumVerbosity ->
            echoStage(
                message,
                verbosityOption,
                minimumVerbosity
            )
        },
        echoSuccess = { message, verbosityOption, minimumVerbosity ->
            echoSuccess(
                message,
                verbosityOption,
                minimumVerbosity
            )
        },
        echoWarning = { message, verbosityOption, minimumVerbosity ->
            echoWarning(
                message,
                verbosityOption,
                minimumVerbosity
            )
        }
    )

private val logger = KotlinLogging.logger {}

fun CliktCommand.handleError(e: PunktError) {
    echoError(e.message)
    logger.error { e.message }
    throw ProgramResult(e.statusCode)
}