package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.hub.command.options.ListOptions
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class ListOperation(
    val globalOptions: GlobalOptions,
    val listOptions: ListOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
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
                    ignoreUnknownKeys = true
                    explicitNulls = false
                })
            }
        }.use { client ->
            try {
                client.get(
                    configuration.hub.serverUrl + "/collections",
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Listing dotfiles on Hub",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}