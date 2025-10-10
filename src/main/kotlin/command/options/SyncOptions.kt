package com.an5on.command.options

data class SyncOptions (
    val recursive: Boolean,
    val include: Regex,
    val exclude: Regex
)