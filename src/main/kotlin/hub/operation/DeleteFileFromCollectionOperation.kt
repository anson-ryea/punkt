package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*

/**
 * Operation that deletes a single dotfile from a Punkt Hub collection.
 *
 * It performs an authenticated `DELETE` request for the given file within
 * the specified collection handle.
 *
 * @property globalOptions Global CLI options controlling verbosity and behaviour.
 * @property handle Integer handle of the collection containing the file.
 * @property fileName Name of the dotfile to delete from the collection.
 * @property echos Helper used to print messages to the user.
 * @property terminal Terminal used for console output.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class DeleteFileFromCollectionOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val fileName: String,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {

    /**
     * Ensures that the user is authenticated before deleting a dotfile.
     *
     * Fails with [HubError.LoggedOut] if no access token is present.
     *
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LoginOperation.getToken() != null) {
            HubError.LoggedOut()
        }
    }

    /**
     * Issues the HTTP `DELETE` call to remove the dotfile from the collection.
     *
     * Network timeouts are surfaced as [HubError.ServerTimeout]. Non-success HTTP
     * responses are wrapped in [HubError.OperationFailed] including status details.
     *
     * @param fromBefore Value from [runBefore]; unused.
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = either {
        HttpClient(CIO) {
            expectSuccess = true
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(LoginOperation.getToken()!!, "")
                    }
                }
            }
            engine {
                requestTimeout = 30_000

                endpoint {
                    connectTimeout = 10_000
                    connectAttempts = 3
                }
            }
        }.use { client ->
            try {
                client.delete(
                    ActiveConfiguration.configuration.hub.serverUrl + "/collections/${handle}/dotfiles/${fileName}",
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Delete File from Collection",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}