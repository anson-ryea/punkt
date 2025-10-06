package com.an5on.utils

class Echos (
    val echo: (Any?, Boolean, Boolean) -> Unit,
    val echoStage: (Any?) -> Unit,
    val echoSuccess: (Any?) -> Unit,
    val echoWarning: (Any?) -> Unit,
)