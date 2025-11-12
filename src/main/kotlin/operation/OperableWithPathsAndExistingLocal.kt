package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.CommonOptions
import com.an5on.command.options.GlobalOptions
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState
import com.github.ajalt.clikt.parameters.groups.OptionGroup
import com.github.ajalt.mordant.terminal.Terminal
import java.nio.file.Path

abstract class OperableWithPathsAndExistingLocal(
    protected val activePaths: Set<Path>?,
    protected val globalOptions: GlobalOptions,
    protected val commonOptions: CommonOptions,
    protected val specificOptions: OptionGroup,
    protected val echos: Echos,
    protected val terminal: Terminal,
) : Operable {
    override fun operate() = either<PunktError, Unit> {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        if (activePaths.isNullOrEmpty()) {
            operateWithExistingLocal()
        } else {
            operateWithPaths(activePaths)
        }
    }

    abstract fun operateWithExistingLocal(): Either<PunktError, Unit>

    abstract fun operateWithPaths(paths: Set<Path>): Either<PunktError, Unit>
}