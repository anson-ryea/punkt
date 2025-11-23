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

/**
 * Operation that retrieves and displays the dotfiles for a specific Punkt Hub collection.
 *
 * It fetches the list of [Dotfile] entries by collection handle and renders them
 * as a table to the configured [terminal].
 *
 * @property globalOptions Global CLI options controlling verbosity.
 * @property handle Integer handle of the collection to inspect.
 * @property echos Helper for user-facing messaging.
 * @property terminal Terminal used to render the resulting table.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class GetCollectionByIdOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, List<Dotfile>, Unit> {

    /**
     * Ensures that the current user is logged in before querying the collection.
     *
     * Fails with [HubError.LoggedOut] if no access token is present.
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
     * Retrieves the list of dotfiles belonging to the target collection.
     *
     * The response body is deserialised into a [List] of [Dotfile]. HTTP and timeout
     * failures are converted into appropriate [HubError] variants.
     *
     * @param fromBefore Value from [runBefore]; unused.
     * @return An [Either] containing [PunktError] on failure or a list of [Dotfile] on success.
     *
     * @since 0.1.0
     */
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

    /**
     * Renders the retrieved dotfiles as a table printed to the terminal.
     *
     * Each row shows the dotfile name and its path.
     *
     * @param fromOperate The list of [Dotfile] instances returned by [operate].
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
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