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
import org.apache.commons.io.file.PathUtils
import java.io.File
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.zip.ZipInputStream

class SyncOperation(
    val globalOptions: GlobalOptions,
    val handle: Int?,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
    private val tempDownloadedZipFile: File = File.createTempFile("punkt_collection_$handle", ".zip")
    private val unzipPath: Path = Files.createTempDirectory("punkt_unzip_$handle")

    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }
        ensure(getToken() != null) {
            HubError.LoggedOut()
        }
    }

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

                val destPath = configuration.global.localStatePath.resolve(".punkthub/collections")
                Files.createDirectories(destPath)
                PathUtils.copyDirectory(unzipPath, destPath,
                    StandardCopyOption.REPLACE_EXISTING)
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "List collections",
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
}