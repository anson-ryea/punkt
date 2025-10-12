package com.an5on.command

import com.github.ajalt.clikt.core.CliktCommand

/**
 * Echoes a stage message with blue color and arrow.
 *
 * @param message the message to echo
 */
fun CliktCommand.echoStage(message: Any?) = this.echo("\u001B[1m\u001B[34m~~>\u001B[39m $message\u001B[0m", err = false)

/**
 * Echoes a success message with green color and checkmark.
 *
 * @param message the message to echo, defaults to "Done!"
 */
fun CliktCommand.echoSuccess(message: Any? = "Done!") =
    this.echo("\u001B[32m :>\u001B[39m $message\u001B[0m", err = false)

/**
 * Echoes a warning message with yellow color and warning symbol.
 *
 * @param message the message to echo
 */
fun CliktCommand.echoWarning(message: Any?) = this.echo("\u001B[1m\u001B[33m :| $message\u001B[0m", err = false)