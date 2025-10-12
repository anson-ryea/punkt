package com.an5on.command

data class Echos(
    val echo: (message: Any?, trailingNewLine: Boolean, err: Boolean) -> Unit,
    val echoStage: (message: Any?) -> Unit,
    val echoSuccess: (message: Any?) -> Unit,
    val echoWarning: (message: Any?) -> Unit,
)