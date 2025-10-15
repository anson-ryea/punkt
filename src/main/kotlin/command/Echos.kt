package com.an5on.command

import com.an5on.type.VerbosityType

/**
 * Data class holding echo functions for outputting messages in different styles.
 *
 * @property echoWithVerbosity the general echo function for messages
 * @property echoStage the function for stage messages
 * @property echoSuccess the function for success messages
 * @property echoWarning the function for warning messages
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class Echos(
    val echoWithVerbosity: (message: Any?, trailingNewLine: Boolean, err: Boolean, verbosityOption: VerbosityType?, minimumVerbosity: VerbosityType) -> Unit,
    val echoStage: (message: Any?, verbosityOption: VerbosityType?, minimumVerbosity: VerbosityType) -> Unit,
    val echoSuccess: (message: Any?, verbosityOption: VerbosityType?, minimumVerbosity: VerbosityType) -> Unit,
    val echoWarning: (message: Any?, verbosityOption: VerbosityType?, minimumVerbosity: VerbosityType) -> Unit,
)