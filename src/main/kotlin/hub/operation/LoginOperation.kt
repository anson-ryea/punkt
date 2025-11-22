package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.command.options.LoginOptions
import com.an5on.hub.error.HubError
import com.an5on.hub.type.TokenResponse
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import kotlin.io.path.createParentDirectories
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText

class LoginOperation(
    val globalOptions: GlobalOptions,
    val loginOptions: LoginOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable <Unit, Unit, Unit> {
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        echos.echoStage(
            "Logging in as ${loginOptions.email}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
        ensure(!validateToken().bind()) {
            HubError.AlreadyLoggedIn()
        }
    }

    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = either {
        HttpClient(CIO) {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    explicitNulls = false
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
                val response = client.submitForm(
                    configuration.hub.serverUrl + "/auth/token",
                    formParameters = parameters {
                        append("username", loginOptions.email)
                        append("password", loginOptions.password)
                    }
                )

                val tokenResponse = response.body<TokenResponse>()

                setToken(tokenResponse)
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Login",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }

    companion object {
        private val tokenPath = configuration.hub.tokenPath
        private val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        private fun setToken(tokenResponse: TokenResponse) {
            tokenPath.createParentDirectories()
            tokenPath.writeText(json.encodeToString(tokenResponse))
        }

        fun getToken(): String? {
            if (!tokenPath.exists()) return null
            return try {
                json.decodeFromString<TokenResponse>(tokenPath.readText()).accessToken
            } catch (e: Exception) {
                null
            }
        }

        suspend fun validateToken(): Either<PunktError, Boolean> = either {
            getToken() ?: return@either false

            HttpClient(CIO) {
                expectSuccess = true
                install(Auth) {
                    bearer {
                        loadTokens {
                            BearerTokens(getToken()!!, "")
                        }
                    }
                }
            }.use { client ->
                try {
                    client.get(
                        configuration.hub.serverUrl + "/users/me",
                    )
                } catch (e: HttpRequestTimeoutException) {
                    raise(HubError.ServerTimeout(e))
                } catch (e: ResponseException) {
                    removeToken()
                    return@either false
                } finally {
                    client.close()
                }
            }
            return@either true
        }

        private fun removeToken() {
            if (tokenPath.exists()) {
                tokenPath.toFile().delete()
            }
        }
    }
}