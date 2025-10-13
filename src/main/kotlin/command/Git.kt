package com.an5on.command

import arrow.core.raise.fold
import com.an5on.git.system.GitOperation.git
import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class Git: CliktCommand() {
    private val arguments by argument().multiple()

    override fun run() {
        fold(
            { git(arguments) },
            {},
            {}
            )
    }
}