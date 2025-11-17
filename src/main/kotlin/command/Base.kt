package com.an5on.command

import com.an5on.punkt.BuildConfig.APP_VERSION
import com.github.ajalt.clikt.command.SuspendingCliktCommand
import com.github.ajalt.clikt.parameters.options.versionOption

/**
 * The base command for Punkt.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object Base : SuspendingCliktCommand(name = "punkt") {
    init {
        versionOption(APP_VERSION)
    }

    override suspend fun run() = Unit
}