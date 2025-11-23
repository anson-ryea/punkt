package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.command.options.ActivateLicenceOptions
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlin.collections.mapOf

/**
 * Operation that activates a Punkt Hub licence for the currently authenticated user.
 *
 * This operation requires an existing authentication token and calls the Hub API
 * to activate the licence key supplied via [activateLicenceOptions].
 *
 * @property globalOptions Global CLI options that influence logging and verbosity.
 * @property activateLicenceOptions Options containing the licence key to activate.
 * @property echos Helper used to render messages to the user.
 * @property terminal Terminal used for low-level output.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class ActivateLicenceOperation(
    val globalOptions: GlobalOptions,
    val activateLicenceOptions: ActivateLicenceOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable <Unit, Unit, Unit> {

    /**
     * Verifies that the user is logged in before attempting licence activation.
     *
     * Fails with [HubError.LoggedOut] if no access token is available.
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
     * Calls the Hub API to activate the provided licence key.
     *
     * A bearer token is attached to the request, and HTTP errors are mapped to [HubError]
     * instances with descriptive messages. Timeouts are surfaced as [HubError.ServerTimeout].
     *
     * @param fromBefore Value produced by [runBefore]; unused but required by the interface.
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
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
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
                client.post(
                    configuration.hub.serverUrl + "/users/me/license-activate",
                ) {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "key_string" to activateLicenceOptions.key,
                        )
                    )
                }
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Activate Licence",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}