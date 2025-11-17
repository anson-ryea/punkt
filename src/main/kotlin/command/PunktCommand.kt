package com.an5on.command

import com.an5on.error.PunktError
import com.an5on.type.Verbosity
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.mordant.rendering.TextColors
import com.github.ajalt.mordant.rendering.TextStyles
import com.github.ajalt.mordant.terminal.Terminal
import com.github.ajalt.mordant.terminal.YesNoPrompt
import io.github.oshai.kotlinlogging.KotlinLogging

/**
 * An abstract base class for all `punkt` commands, providing common functionality for output and error handling.
 *
 * This class extends `CliktCommand` and offers a standardised set of methods for echoing styled messages
 * (e.g., stages, successes, warnings, errors) to the console, respecting verbosity levels. It also includes
 * a consistent mechanism for handling `PunktError` exceptions and terminating the program with an appropriate
 * status code.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
abstract class PunktCommand : SuspendingCliktCommand() {
    private val logger = KotlinLogging.logger {}

    /**
     * Prints a message to the console, but only if the current verbosity level is at or above the specified minimum.
     *
     * @param message The message to print.
     * @param trailingNewLine Whether to append a newline to the message.
     * @param err Whether to print to the standard error stream instead of standard output.
     * @param verbosityOption The current verbosity level set for the command.
     * @param minimumVerbosity The minimum verbosity level required to display the message.
     */
    protected fun echoWithVerbosity(
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
     * Displays a formatted stage message, indicating the start of a process or step.
     *
     * The message is styled to be easily recognisable as a stage marker. Output is subject to verbosity settings.
     *
     * @param message The message to display.
     * @param verbosityOption The current verbosity level.
     * @param minimumVerbosity The minimum verbosity required for the message to be shown.
     */
    protected fun echoStage(
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
     * Displays a formatted success message, typically upon successful completion of an operation.
     *
     * The message is styled to indicate success (e.g., with a checkmark and distinct colour).
     * Output is subject to verbosity settings.
     *
     * @param message The message to display. Defaults to "Done!".
     * @param verbosityOption The current verbosity level.
     * @param minimumVerbosity The minimum verbosity required for the message to be shown.
     */
    protected fun echoSuccess(
        message: Any? = "Done!",
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(message.toString().prependSuccess())
    }

    /**
     * Displays a formatted warning message to alert the user to a potential issue.
     *
     * The message is styled to draw attention (e.g., with a warning symbol and colour).
     * Output is subject to verbosity settings.
     *
     * @param message The warning message to display.
     * @param verbosityOption The current verbosity level.
     * @param minimumVerbosity The minimum verbosity required for the message to be shown.
     */
    protected fun echoWarning(
        message: Any?,
        verbosityOption: Verbosity,
        minimumVerbosity: Verbosity = Verbosity.NORMAL
    ) {
        if (verbosityOption < minimumVerbosity) return

        echo(
            TextColors.yellow(message.toString().prependWaring())
        )
    }

    /**
     * Displays a formatted error message to the standard error stream.
     *
     * This method is not subject to verbosity settings and will always display the error.
     *
     * @param message The error message to display.
     */
    protected fun echoError(
        message: Any?
    ) {
        echo(
            TextStyles.bold(TextColors.red(message.toString().prependError())),
            err = true
        )
    }

    /**
     * Provides a convenient [Echos] instance containing the specialised output functions.
     *
     * This allows passing a consistent set of styled output functions to other parts of the application.
     */
    protected val echos: Echos
        get() = Echos(
            ::echoWithVerbosity,
            ::echoStage,
            ::echoSuccess,
            ::echoWarning,
        )

    /**
     * Handles a [PunktError] by printing a formatted error message, logging it, and terminating the program.
     *
     * @param e The [PunktError] to handle.
     */
    protected fun handleError(e: PunktError) {
        echoError(e.message)
        logger.error { e.message }
        throw ProgramResult(e.statusCode)
    }

    /**
     * A companion object containing utility functions and constants for styling command-line output.
     *
     * This object provides a set of extension functions for `String` to prepend various styled prefixes,
     * such as prompts, warnings, errors, and stage markers. It also defines a factory function for creating
     * a standardised `YesNoPrompt`.
     *
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    companion object {
        private const val CONTENT_INDENTATION = "    " // 4 spaces
        private const val PROMPT_INDENTATION = " :? "
        private const val WARNING_INDENTATION = " :! "
        private const val ERROR_INDENTATION = " :< "
        private const val SUCCESS_INDENTATION = " :> "
        private const val STAGE_INDENTATION = "~~> "

        /**
         * Indents the string with a standard amount of whitespace.
         */
        fun String.indented() = prependIndent(CONTENT_INDENTATION)

        /**
         * Prepends a styled prompt prefix to the string.
         */
        fun String.prependPrompt() = prependIndent(PROMPT_INDENTATION)

        /**
         * Prepends a styled warning prefix to the string.
         */
        fun String.prependWaring() = prependIndent(WARNING_INDENTATION)

        /**
         * Prepends a styled error prefix to the string.
         */
        fun String.prependError() = prependIndent(ERROR_INDENTATION)

        /**
         * Prepends a styled success prefix to the string, typically with colour.
         */
        fun String.prependSuccess() = prependIndent(TextColors.green(SUCCESS_INDENTATION))

        /**
         * Prepends a styled stage prefix to the string, typically with colour.
         */
        fun String.prependStage() = prependIndent(TextColors.cyan(STAGE_INDENTATION))

        /**
         * Creates a standardised `YesNoPrompt` with consistent styling for user confirmation.
         *
         * @param prompt The question to ask the user.
         * @param terminal The terminal instance to render the prompt on.
         * @return A `YesNoPrompt` instance ready to be displayed.
         */
        fun punktYesNoPrompt(
            prompt: String,
            terminal: Terminal
        ) = YesNoPrompt(
            TextStyles.bold(
                TextColors.yellow(
                    prompt.prependPrompt()
                )
            ),
            terminal
        )
    }
}