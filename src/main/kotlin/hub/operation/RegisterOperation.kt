package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.an5on.hub.type.RegisterPayload
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.StringPrompt
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
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable {
    override suspend fun operate(): Either<PunktError, Unit> = either {
        echos.echoStage(
            "Registering a new account on ${configuration.hub.serverUrl}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
        val username = StringPrompt(
            "Username",
            terminal,
            allowBlank = false
        ).ask()
        val email = StringPrompt(
            "Email",
            terminal,
            allowBlank = false
        ).ask()
        val password = StringPrompt(
            "Password",
            terminal,
            allowBlank = false
        ).ask()

        ensure(!username.isNullOrBlank() && !email.isNullOrBlank() && !password.isNullOrBlank()) {
            HubError.RegisterFailed("Username, email, and password must not be empty.")
        }

        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    prettyPrint = true
                    isLenient = true
                })
            }
        }.use { client ->
            try {
                client.post(configuration.hub.serverUrl + "/users/register") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        RegisterPayload(
                            "audrey",
                            "a@g.c",
                            "audrey"
                        )
                    )
                }

                echos.echoSuccess(
                    "Welcome to Punkt Hub! You can now log in with $email.",
                    globalOptions.verbosity,
                    Verbosity.NORMAL
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(HubError.RegisterFailed("${e.response.status.value} - ${e.response.status.description}"))
            } finally {
                client.close()
            }
        }
    }
}