package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration
import com.an5on.error.PunktError
import com.an5on.hub.error.HubError
import com.github.ajalt.mordant.terminal.Terminal
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.*

class DeleteFileFromCollectionOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val fileName: String,
    val echos: Echos,
    val terminal: Terminal
) : SuspendingOperable<Unit, Unit, Unit> {
    override suspend fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LoginOperation.getToken() != null) {
            HubError.LoggedOut()
        }
    }

    override suspend fun operate(fromBefore: Unit): Either<PunktError, Unit> = either {
        HttpClient(CIO) {
            expectSuccess = true
            install(Auth) {
                bearer {
                    loadTokens {
                        BearerTokens(LoginOperation.getToken()!!, "")
                    }
                }
            }
        }.use { client ->
            try {
                client.delete(
                    ActiveConfiguration.configuration.hub.serverUrl + "/collections/${handle}/dotfiles/${fileName}",
                )
            } catch (e: HttpRequestTimeoutException) {
                raise(HubError.ServerTimeout(e))
            } catch (e: ResponseException) {
                raise(
                    HubError.OperationFailed(
                        "Delete File from Collection",
                        "${e.response.status.value} - ${e.response.status.description}"
                    )
                )
            } finally {
                client.close()
            }
        }
    }
}