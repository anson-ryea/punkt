package com.an5on.command

import com.an5on.type.Verbosity

/**
 * A data class that encapsulates a set of functions for displaying styled and verbosity-controlled output to the console.
 *
 * This class is used to pass a consistent set of output functions to various parts of the application, allowing
 * them to produce formatted messages for different purposes (e.g., stages, successes, warnings) while respecting
 * global verbosity settings.
 *
 * @property echoWithVerbosity A general-purpose function for printing messages with fine-grained control over newlines,
 * error streams, and verbosity levels.
 * @property echoStage A specialised function for displaying messages that indicate a stage or step in a process.
 * @property echoSuccess A specialised function for displaying success messages, often with distinct styling.
 * @property echoWarning A specialised function for displaying warning messages, helping to draw user attention to
 * potential issues.
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class Echos(
    val echoWithVerbosity: (message: Any?, trailingNewLine: Boolean, err: Boolean, verbosityOption: Verbosity, minimumVerbosity: Verbosity) -> Unit,
    val echoStage: (message: Any?, verbosityOption: Verbosity, minimumVerbosity: Verbosity) -> Unit,
    val echoSuccess: (message: Any?, verbosityOption: Verbosity, minimumVerbosity: Verbosity) -> Unit,
    val echoWarning: (message: Any?, verbosityOption: Verbosity, minimumVerbosity: Verbosity) -> Unit,
)