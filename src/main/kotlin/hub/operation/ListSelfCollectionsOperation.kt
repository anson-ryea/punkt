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
import com.an5on.hub.type.Collection
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
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlin.time.ExperimentalTime

class ListSelfCollectionsOperation(
    val globalOptions: GlobalOptions,
    val handle: Int?,
    val echos: Echos,
    val terminal: Terminal,
) : SuspendingOperable<Unit, List<Collection>, Unit> {
    @OptIn(ExperimentalTime::class)
    override suspend fun operate(fromBefore: Unit): Either<PunktError, List<Collection>> = either {
        var selfCollections: List<Collection>

        val httpClient = HttpClient(CIO) {
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
        }

        httpClient.use { client ->
            try {
                val response = client.get(
                    configuration.hub.serverUrl + "/collections/owned",
                )

                selfCollections = response.body<List<Collection>>()
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "List collections owned by you",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }

        if (handle != null) {
            ensure(selfCollections.any { it.id == handle }) {
                HubError.OperationFailed(
                    "List collections owned by you",
                    "Collection with handle $handle not found or it does not belong to you"
                )
            }
            GetCollectionByIdOperation(globalOptions, handle, echos, terminal).let { it ->
                it.runAfter(it.operate(Unit).bind())
            }.bind()
        } else {
            terminal.println(
                table {
                    borderType = BorderType.ASCII_DOUBLE_SECTION_SEPARATOR
                    tableBorders = Borders.NONE
                    header {
                        row("name", "description", "handle", "last updated")
                    }
                    body {
                        selfCollections.forEach { collection ->
                            row(
                                collection.name,
                                collection.description,
                                collection.id,
                                collection.updatedAt.toLocalDateTime(
                                    TimeZone.currentSystemDefault()
                                ).date.toString()
                            )
                        }
                    }
                }
            )
        }

        return@either selfCollections
    }
}