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
import com.an5on.hub.type.Dotfile
import com.github.ajalt.mordant.rendering.BorderType
import com.github.ajalt.mordant.table.Borders
import com.github.ajalt.mordant.table.table
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

class GetCollectionByIdOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, List<Dotfile>, Unit> {
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

    override suspend fun operate(fromBefore: Unit): Either<PunktError, List<Dotfile>> = either {
        var collection: List<Dotfile>

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
                val response = client.get(
                    configuration.hub.serverUrl + "/collections/${handle}/dotfiles",
                )

                collection = response.body<List<Dotfile>>()
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Get collection by handle",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }

        return@either collection
    }

    override suspend fun runAfter(fromOperate: List<Dotfile>): Either<PunktError, Unit> = either {
        terminal.println(
            table {
                borderType = BorderType.ASCII_DOUBLE_SECTION_SEPARATOR
                tableBorders = Borders.NONE
                header {
                    row("file name", "path")
                }
                body {
                    fromOperate.forEach { dotfile ->
                        row(dotfile.fileName, dotfile.pathname)
                    }
                }
            }
        )
    }
}