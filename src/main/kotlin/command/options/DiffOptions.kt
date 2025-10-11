package com.an5on.command.options

data class DiffOptions(
    val recursive: Boolean,
    val include: Regex,
    val exclude: Regex
)