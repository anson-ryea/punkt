package com.an5on.command.options

import com.an5on.operation.PathStyles

data class ListOptions (
    val include: Regex,
    val exclude: Regex,
    val pathStyle: PathStyles
)