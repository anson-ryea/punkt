package com.an5on.utils

import com.github.ajalt.clikt.core.CliktCommand

fun CliktCommand.echoStage(message: Any?) = this.echo("\u001B[1m\u001B[34m~~>\u001B[39m $message\u001B[0m", err = false)

fun CliktCommand.echoSuccess(message: Any? = "Done!") = this.echo("\u001B[32m :>\u001B[39m $message\u001B[0m", err = false)

fun CliktCommand.echoWarning(message: Any?) = this.echo("\u001B[1m\u001B[33m :| $message\u001B[0m", err = false)