package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.command.options.CreateCollectionOptions
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.an5on.hub.type.CreateCollectionRequest
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class CreateCollectionOperation(
    val globalOptions: GlobalOptions,
    val createCollectionOptions: CreateCollectionOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = either {
        HttpClient(CIO) {
            // Do not throw on non-2xx so we can inspect 307 Temporary Redirect and other statuses
            expectSuccess = false
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
                val requestBody = CreateCollectionRequest(
                    createCollectionOptions.name,
                    createCollectionOptions.description,
                    createCollectionOptions.private
                )

                // Initial request
                var response = client.post(configuration.hub.serverUrl + "/collections") {
                    contentType(ContentType.Application.Json)
                    setBody(requestBody)
                }

                // Handle 307 Temporary Redirect by following Location header and retrying
                if (response.status == HttpStatusCode.TemporaryRedirect) {
                    val location = response.headers[HttpHeaders.Location]
                    ensure(!location.isNullOrBlank()) {
                        HubError.OperationFailed(
                            "Create Collection",
                            "307 received but no Location header provided"
                        )
                    }

                    // Retry to the location preserving method and body
                    response = client.post(location) {
                        contentType(ContentType.Application.Json)
                        setBody(requestBody)
                    }
                }

                ensure(response.status.isSuccess()) {
                    // Try to include status and description
                    HubError.OperationFailed(
                        "Create Collection",
                        "${response.status.value} - ${response.status.description}"
                    )
                }
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            }
        }
    }
}