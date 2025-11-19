package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.Either.Companion.catchOrThrow
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.github.ajalt.mordant.terminal.Terminal
import java.io.IOException
import java.nio.file.Files

class LogoutOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable <Unit, Unit, Unit> {
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = catchOrThrow<IOException, Unit> {
        Files.deleteIfExists(configuration.hub.tokenPath)
    }.mapLeft {
        HubError.OperationFailed("Logout", "Failed to delete token file: ${it.message}")
    }
}