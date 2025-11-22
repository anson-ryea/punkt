package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.an5on.hub.operation.LoginOperation.Companion.getToken
import com.an5on.states.local.LocalState
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
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream
import kotlin.io.path.exists

class SyncOperation(
    val globalOptions: GlobalOptions,
    var handle: Int?,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
    private val tempDownloadedZipFile: File = File.createTempFile("punkt_collection_$handle", ".zip")
    private val unzipPath: Path = Files.createTempDirectory("punkt_unzip_$handle")
    private val collectionsPath: Path = configuration.global.localStatePath.resolve(".punkthub/collections")

    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = either {
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        // Get collection metadata
        val collection = GetCollectionByIdOperation(globalOptions, handle!!, echos, terminal).operate(Unit).bind()
        val collectionInJson = json.encodeToString(collection)
        val metadataFile = collectionsPath.resolve("c$handle.json").toFile()
        FileUtils.createParentDirectories(metadataFile)
        metadataFile.writeText(collectionInJson)

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
                    configuration.hub.serverUrl + "/collections/$handle/archive",
                ) {
                    onDownload { bytesSentTotal, contentLength ->
                        echos.echoWithVerbosity(
                            "Received $bytesSentTotal bytes from $contentLength",
                            true,
                            false,
                            globalOptions.verbosity,
                            Verbosity.NORMAL
                        )
                    }
                }

                tempDownloadedZipFile.writeBytes(response.body())

                ZipInputStream(FileInputStream(tempDownloadedZipFile)).use { zis ->
                    var entry = zis.nextEntry
                    while (entry != null) {
                        val filePath = unzipPath.resolve(entry.name)
                        if (entry.isDirectory) {
                            Files.createDirectories(filePath)
                        } else {
                            Files.createDirectories(filePath.parent)
                            Files.newOutputStream(filePath).use { zis.copyTo(it) }
                        }
                        zis.closeEntry()
                        entry = zis.nextEntry
                    }
                }

                Files.createDirectories(collectionsPath)
                PathUtils.copyDirectory(
                    unzipPath, collectionsPath,
                    StandardCopyOption.REPLACE_EXISTING
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Sync collection",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } catch (e: Exception) {
                raise(HubError.OperationFailed("Unzipping collection archive", e.message ?: "Unknown error"))
            } finally {
                tempDownloadedZipFile.delete()
                client.close()
            }
        }
    }

    override suspend fun run(): Either<PunktError, Unit> = either {
        runBefore().bind()

        if (handle == null) {
            if (collectionsPath.exists()) {
                FileUtils.listFiles(collectionsPath.toFile(), arrayOf("json"), false).forEach { file ->
                    val fileName = file.nameWithoutExtension
                    if (fileName.startsWith("c")) {
                        val collectionId = fileName.removePrefix("c").toIntOrNull()
                        if (collectionId != null) {
                            handle = collectionId
                            echos.echoStage(
                                "Syncing collection with ID: $handle",
                                globalOptions.verbosity,
                                Verbosity.NORMAL
                            )
                            operate(Unit).bind()
                        }
                    }
                }
            }
        } else {
            echos.echoStage(
                "Syncing collection with ID: $handle",
                globalOptions.verbosity,
                Verbosity.NORMAL
            )
            operate(Unit).bind()
        }
    }
}