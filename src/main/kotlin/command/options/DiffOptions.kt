package com.an5on.command.options

/**
 * Options for the diff command.
 *
 * @property recursive whether to process directories recursively
 * @property include regex pattern for including files
 * @property exclude regex pattern for excluding files
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class DiffOptions(
    val recursive: Boolean,
    val include: Regex,
    val exclude: Regex
)