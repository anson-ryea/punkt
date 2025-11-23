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

/**
 * Operation that removes a collection's cached state from the local Punkt directory.
 *
 * It deletes both the collection metadata file and its unpacked directory for the
 * specified handle, effectively stopping local synchronisation.
 *
 * @property globalOptions Global CLI options affecting verbosity.
 * @property handle Handle of the collection to unsynchronise.
 * @property echos Helper used to output stage messages.
 * @property terminal Terminal used for console interaction.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class UnsyncOperation(
    val globalOptions: GlobalOptions,
    val handle: Int,
    val echos: Echos,
    val terminal: Terminal
) : Operable {
    private val collectionsPath: Path = configuration.global.localStatePath.resolve(".punkthub/collections")
    private val collections = FileUtils.listFiles(collectionsPath.toFile(), arrayOf("json"), false)

    /**
     * Ensures that local state exists and that the target collection is currently synced.
     *
     * If the collection has no local metadata file, the operation fails with
     * [HubError.OperationFailed].
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
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

    /**
     * Removes the collection's metadata file and extracted directory, if present.
     *
     * Both the `c<handle>.json` file and the `c<handle>` directory are deleted.
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override fun operate(): Either<PunktError, Unit> = either {
        if (collectionsPath.resolve("c$handle.json").exists()) {
            FileUtils.delete(collectionsPath.resolve("c$handle.json").toFile())
        }

        if (collectionsPath.resolve("c$handle").exists() && collectionsPath.resolve("c$handle").isDirectory()) {
            FileUtils.deleteDirectory(collectionsPath.resolve("c$handle").toFile())
        }
    }
}