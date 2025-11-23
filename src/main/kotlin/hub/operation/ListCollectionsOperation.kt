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

/**
 * Operation that lists public Punkt Hub collections and prints them in a table.
 *
 * Collections are fetched from the `/collections/public` endpoint and then displayed
 * with basic metadata such as name, description, handle, and last updated date.
 *
 * @property globalOptions Global CLI options affecting verbosity.
 * @property echos Helper used for user-facing messages.
 * @property terminal Terminal used for rendering the collections table.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class ListCollectionsOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, List<Collection>, Unit> {

    /**
     * Ensures that the user is authenticated before listing collections.
     *
     * Fails with [HubError.LoggedOut] when no access token is available.
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
     * Retrieves all public collections from Punkt Hub.
     *
     * The HTTP response body is deserialised into a [List] of [Collection] objects.
     * Timeouts and HTTP failures are reported using [HubError].
     *
     * @param fromBefore Value produced by [runBefore]; unused.
     * @return An [Either] containing [PunktError] on failure or a list of [Collection] on success.
     *
     * @since 0.1.0
     */
    override suspend fun operate(fromBefore: Unit): Either<PunktError, List<Collection>> = either {
        var collections: List<Collection>

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
            engine {
                requestTimeout = 30_000

                endpoint {
                    connectTimeout = 10_000
                    connectAttempts = 3
                }
            }
        }.use { client ->
            try {
                val response = client.get(
                    configuration.hub.serverUrl + "/collections/public",
                )

                collections = response.body<List<Collection>>()
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "List collections",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }

        return@either collections
    }

    /**
     * Renders the retrieved collections in a tabular format on the terminal.
     *
     * Each row contains the collection name, description, handle, and last updated date
     * converted to the local time zone.
     *
     * @param fromOperate The list of [Collection] instances returned by [operate].
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    @OptIn(ExperimentalTime::class)
    override suspend fun runAfter(fromOperate: List<Collection>): Either<PunktError, Unit> = either {
        terminal.println(
            table {
                borderType = BorderType.ASCII_DOUBLE_SECTION_SEPARATOR
                tableBorders = Borders.NONE
                header {
                    row("name", "description", "handle", "last updated")
                }
                body {
                    fromOperate.forEach { collection ->
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
}