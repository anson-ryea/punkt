package com.an5on.operation

data class ListOptions (
    val include: Regex,
    val exclude: Regex,
    val pathStyle: PathStyles
)