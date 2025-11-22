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
import com.an5on.operation.Operable
import com.an5on.states.local.LocalState
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import org.apache.commons.io.FileUtils
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

class UnsyncOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val echos: Echos,
    val terminal: Terminal
) : Operable {
    private val collectionsPath: Path = configuration.global.localStatePath.resolve(".punkthub/collections")
    private val collections = FileUtils.listFiles(collectionsPath.toFile(), arrayOf("json"), false)

    override fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        ensure(collections.any { it.nameWithoutExtension == "c$handle" }) {
            HubError.OperationFailed("Unsync", "Collection with handle $handle is not synced.")
        }

        echos.echoStage(
            "Unsyncing collection c$handle from local state",
            globalOptions.verbosity,
            Verbosity.NORMAL
        )
    }

    override fun operate(): Either<PunktError, Unit> = either {
        if (collectionsPath.resolve("c$handle.json").exists()) {
            FileUtils.delete(collectionsPath.resolve("c$handle.json").toFile())
        }

        if (collectionsPath.resolve("c$handle").exists() && collectionsPath.resolve("c$handle").isDirectory()) {
            FileUtils.deleteDirectory(collectionsPath.resolve("c$handle").toFile())
        }
    }
}