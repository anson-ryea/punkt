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

/**
 * Operation that logs a user into Punkt Hub and persists the received access token.
 *
 * The operation first checks that there is no valid existing session, then performs
 * a password grant against the `/auth/token` endpoint and stores the resulting token
 * on disk for later reuse.
 *
 * @property globalOptions Global CLI options that influence verbosity and behaviour.
 * @property loginOptions Options containing the e-mail address and password to use.
 * @property echos Helper used to render progress and status messages.
 * @property terminal Terminal used for low-level console output.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class LoginOperation(
    val globalOptions: GlobalOptions,
    val loginOptions: LoginOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable <Unit, Unit, Unit> {

    /**
     * Validates that there is no active, valid token before logging in.
     *
     * If a valid token is already present, the operation fails with [HubError.AlreadyLoggedIn].
     * Otherwise, it prints a stage message indicating which account is being used.
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
            "Logging in as ${loginOptions.email}",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
    }

    /**
     * Performs the HTTP password-grant request and saves the returned access token.
     *
     * On success, the [TokenResponse] is serialised to the configured token path.
     * Timeouts and non-success responses are mapped to [HubError] variants.
     *
     * @param fromBefore Value produced by [runBefore]; unused but required by [SuspendingOperable].
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
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

        /**
         * Serialises and writes the given [TokenResponse] to the token file.
         *
         * Parent directories are created if they do not already exist.
         *
         * @param tokenResponse The token payload returned from the Hub authentication endpoint.
         *
         * @since 0.1.0
         */
        private fun setToken(tokenResponse: TokenResponse) {
            tokenPath.createParentDirectories()
            tokenPath.writeText(json.encodeToString(tokenResponse))
        }

        /**
         * Reads the stored token file and returns the access token, if available.
         *
         * Any deserialisation error results in `null` to avoid hard failures on corrupt files.
         *
         * @return The access token string, or `null` if no valid token is present.
         *
         * @since 0.1.0
         */
        fun getToken(): String? {
            if (!tokenPath.exists()) return null
            return try {
                json.decodeFromString<TokenResponse>(tokenPath.readText()).accessToken
            } catch (e: Exception) {
                null
            }
        }

        /**
         * Validates the currently stored token against the `/users/me` endpoint.
         *
         * When the token is missing or invalid, `false` is returned and any stored token
         * is removed on authentication failure. Timeouts are reported as [HubError.ServerTimeout].
         *
         * @return An [Either] containing a [PunktError] on failure or `true`/`false` indicating validity.
         *
         * @since 0.1.0
         */
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

        /**
         * Deletes the stored token file from disk if it exists.
         *
         * @since 0.1.0
         */
        private fun removeToken() {
            if (tokenPath.exists()) {
                tokenPath.toFile().delete()
            }
        }
    }
}