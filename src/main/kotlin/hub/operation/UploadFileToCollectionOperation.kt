package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.toLocal
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.an5on.hub.type.Dotfile
import com.an5on.hub.type.UploadDotfilePayload
import com.an5on.states.local.LocalState
import com.an5on.system.SystemUtils.homePath
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.*
import io.ktor.utils.io.streams.*
import kotlinx.io.buffered
import kotlinx.serialization.json.Json
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.name
import kotlin.io.path.pathString

/**
 * Operation that uploads one or more local files to a Punkt Hub collection.
 *
 * For each active path, the corresponding local path is resolved within the Punkt
 * local state and uploaded to Hub together with a JSON payload describing the
 * dotfiles being added.
 *
 * @property globalOptions Global CLI options that influence verbosity.
 * @property handle Handle of the collection to which files will be uploaded.
 * @property echos Helper used for logging and progress messages.
 * @property terminal Terminal used for HTTP-related output.
 * @property activeLocalPathPairs Pairs of active paths and their mapped local-state paths.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class UploadFileToCollectionOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    targets: Set<Path>,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
    val activeLocalPathPairs = targets.map { it to it.toLocal() }

    /**
     * Validates that local state exists and that all target files resolve within it.
     *
     * Any missing local path results in [LocalError.LocalPathNotFound].
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }
        activeLocalPathPairs.forEach { (activePath, localPath) ->
            ensure(localPath.exists()) {
                LocalError.LocalPathNotFound(activePath)
            }
        }

    }

    /**
     * Builds and submits a multipart request containing metadata and file contents.
     *
     * The JSON part describes the dotfiles being uploaded, while each file is attached
     * as a separate binary part. Network and HTTP errors are mapped to [HubError] variants.
     *
     * @param fromBefore Value produced by [runBefore]; unused.
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    @OptIn(InternalAPI::class)
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
            engine {
                requestTimeout = 30_000

                endpoint {
                    connectTimeout = 10_000
                    connectAttempts = 3
                }
            }
        }.use { client ->
            val jsonPayload = UploadDotfilePayload(
                handle,
                activeLocalPathPairs.map { (activePath, _) ->
                    Dotfile(
                        activePath.pathString.replaceFirst(homePath.pathString, "~").replace("\\", "/"),
                        activePath.name
                    )
                }
            )

            try {
                client.post(
                    configuration.hub.serverUrl + "/collections/$handle/dotfiles",
                ) {
                    setBody(
                        MultiPartFormDataContent(
                            // Build multipart form data
                            formData {
                                // Add the JSON payload as a text part
                                append("collection_add_payload", Json.encodeToString(jsonPayload), Headers.build {
                                    append(HttpHeaders.ContentType, ContentType.Application.Json.toString())
                                })

                                // Add each file as a binary part
                                activeLocalPathPairs.forEach { (activePath, localPath) ->
                                    append(
                                        "files",
                                        InputProvider { localPath.toFile().inputStream().asInput().buffered() },
                                        Headers.build {
                                            append(HttpHeaders.ContentDisposition, "filename=\"${activePath.name}\"")
                                            append(
                                                HttpHeaders.ContentType,
                                                ContentType.Application.OctetStream.toString()
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    )
                }
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Upload file to collection",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}