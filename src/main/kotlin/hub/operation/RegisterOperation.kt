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

class RegisterOperation(
    val globalOptions: GlobalOptions,
    val registerOptions: RegisterOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, String, String> {
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