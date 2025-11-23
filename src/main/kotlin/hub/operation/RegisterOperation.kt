package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.command.options.RegisterOptions
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.validateToken
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

/**
 * Operation that registers a new Punkt Hub account.
 *
 * The operation first checks that there is no valid existing session, then submits
 * the registration payload to the `/users/register` endpoint.
 *
 * @property globalOptions Global CLI options affecting verbosity.
 * @property registerOptions Options providing username, e-mail, and password.
 * @property echos Helper used to display progress and informational messages.
 * @property terminal Terminal used for low-level interaction.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class RegisterOperation(
    val globalOptions: GlobalOptions,
    val registerOptions: RegisterOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, String, String> {

    /**
     * Ensures that the user is not already logged in and announces registration.
     *
     * If a valid token exists, the operation fails with [HubError.AlreadyLoggedIn].
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(!validateToken().bind()) {
            HubError.AlreadyLoggedIn()
        }
        echos.echoStage(
            "Registering a new account on ${configuration.hub.serverUrl}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
    }

    /**
     * Sends the registration request to Punkt Hub.
     *
     * The request is submitted as JSON; timeouts and non-successful responses are mapped
     * to [HubError.ServerTimeout] and [HubError.OperationFailed] respectively. On success,
     * the account e-mail is returned as the intermediate result.
     *
     * @param fromBefore Value produced by [runBefore]; unused.
     * @return An [Either] containing a [PunktError] on failure or the registered e-mail on success.
     *
     * @since 0.1.0
     */
    override suspend fun operate(fromBefore: Unit): Either<PunktError, String> = either {
        HttpClient(CIO) {
            expectSuccess = true
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
                client.post(configuration.hub.serverUrl + "/users/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "email" to registerOptions.email,
                            "password" to registerOptions.password,
                            "username" to registerOptions.username
                        )
                    )
                }
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Register",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }

        return@either registerOptions.email
    }
}