package com.an5on.command.options

import com.an5on.operation.PathStyles

/**
 * Options for the list command.
 *
 * @property include regex pattern for including files
 * @property exclude regex pattern for excluding files
 * @property pathStyle the style for displaying paths
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
data class ListOptions(
    val include: Regex,
    val exclude: Regex,
    val pathStyle: PathStyles
)