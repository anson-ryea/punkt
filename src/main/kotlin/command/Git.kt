package com.an5on.command

import com.an5on.git.GenericOperationWithSystem
import com.github.ajalt.clikt.core.ProgramResult
import com.github.ajalt.clikt.parameters.arguments.argument
import com.github.ajalt.clikt.parameters.arguments.multiple

class Git : PunktCommand() {
    private val arguments by argument().multiple()

    override fun run() {
        GenericOperationWithSystem(arguments)
            .operateWithSystem()
            .fold(
                { handleError(it) },
                {
                    throw ProgramResult(it)
                }
            )
    }
}