package com.an5on.operation

data class ActivateOptions(
    val recursive: Boolean,
    val include: Regex,
    val exclude: Regex
)
