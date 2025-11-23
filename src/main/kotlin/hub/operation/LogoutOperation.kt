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

/**
 * Operation that logs the user out of Punkt Hub by deleting the stored token.
 *
 * This simply ensures that a token exists and then removes the token file from disk.
 *
 * @property globalOptions Global CLI options controlling verbosity and behaviour.
 * @property echos Helper used to print contextual logout messages.
 * @property terminal Terminal used for console I/O.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class LogoutOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable <Unit, Unit, Unit> {

    /**
     * Ensures that there is an active login session before attempting logout.
     *
     * If no token is stored, the operation fails with [HubError.LoggedOut].
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

    /**
     * Deletes the configured token file if it exists.
     *
     * Any [IOException] is mapped to [HubError.OperationFailed] with a descriptive message.
     *
     * @param fromBefore Value produced by [runBefore]; unused.
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = catchOrThrow<IOException, Unit> {
        Files.deleteIfExists(configuration.hub.tokenPath)
    }.mapLeft {
        HubError.OperationFailed("Logout", "Failed to delete token file: ${it.message}")
    }
}