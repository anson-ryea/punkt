package com.an5on.command

/**
 * Data class holding echo functions for outputting messages in different styles.
 *
 * @property echo the general echo function for messages
 * @property echoStage the function for stage messages
 * @property echoSuccess the function for success messages
 * @property echoWarning the function for warning messages
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class Echos(
    val echo: (message: Any?, trailingNewLine: Boolean, err: Boolean) -> Unit,
    val echoStage: (message: Any?) -> Unit,
    val echoSuccess: (message: Any?) -> Unit,
    val echoWarning: (message: Any?) -> Unit,
)