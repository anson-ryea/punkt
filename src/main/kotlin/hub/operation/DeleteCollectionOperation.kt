package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*

/**
 * Operation that deletes a Punkt Hub collection by its handle.
 *
 * It performs an authenticated `DELETE` request against the Hub API and
 * reports HTTP and timeout failures as [HubError] instances.
 *
 * @property globalOptions Global CLI options controlling verbosity.
 * @property handle Integer handle of the collection to delete.
 * @property echos Helper used to print user-facing messages.
 * @property terminal Terminal used for output.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class DeleteCollectionOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {

    /**
     * Ensures that the user is logged in before attempting deletion.
     *
     * Fails with [HubError.LoggedOut] when no access token is available.
     *
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

    /**
     * Sends a `DELETE` request to remove the target collection.
     *
     * Timeouts are mapped to [HubError.ServerTimeout], while non-success HTTP responses
     * are mapped to [HubError.OperationFailed] with the status code and description.
     *
     * @param fromBefore Value produced by [runBefore]; unused.
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
                        BearerTokens(getToken()!!, "")
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
                    configuration.hub.serverUrl + "/collections/${handle}",
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Delete Collection",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}