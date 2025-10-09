package com.an5on.operation

data class DiffOptions (
    val recursive: Boolean,
    val include: Regex,
    val exclude: Regex
)