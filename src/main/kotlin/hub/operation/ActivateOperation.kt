package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.file.FileUtils.expandTildeWithHomePathname
import com.an5on.file.FileUtils.toLocal
import com.an5on.hub.type.Dotfile
import com.an5on.operation.Operable
import com.an5on.states.local.LocalState
import com.an5on.type.Verbosity
import com.github.ajalt.mordant.terminal.Terminal
import kotlinx.serialization.json.Json
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.Path
import kotlin.io.path.exists

/**
 * Operation that activates dotfiles from Punkt Hub collections into the local file system.
 *
 * It reads collection metadata and dotfile archives from the local Hub cache and materialises
 * them at their intended paths, preferring existing local files over remote ones.
 *
 * @property globalOptions Global CLI options controlling verbosity and behaviour.
 * @property echos Helper used to emit informational and warning messages.
 * @property terminal Terminal used for interactive output.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class ActivateOperation(
    val globalOptions: GlobalOptions,
    val echos: Echos,
    val terminal: Terminal
) : Operable {
    private val collectionsPath: Path = configuration.global.localStatePath.resolve(".punkthub/collections")

    /**
     * Ensures that the local state exists before attempting activation.
     *
     * Fails with [LocalError.LocalNotFound] if the Punkt local state directory
     * has not been initialised.
     *
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override fun runBefore(): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }
    }

    /**
     * Restores dotfiles for each cached collection into their target locations.
     *
     * For every collection descriptor found under `.punkthub/collections`, this:
     *  * Deserialises the list of [Dotfile] metadata.
     *  * Skips any dotfile that already exists locally, warning the user.
     *  * Copies the stored file to the requested absolute path, creating parents as needed.
     *
     * @return An [Either] containing [PunktError] on failure or `Unit` on success.
     *
     * @since 0.1.0
     */
    override fun operate(): Either<PunktError, Unit> = either {
        if (collectionsPath.exists()) {
            FileUtils.listFiles(collectionsPath.toFile(), arrayOf("json"), false).forEach { file ->
                val fileName = file.nameWithoutExtension
                if (fileName.startsWith("c")) {
                    val dotfilesMetadata = Json.decodeFromString<List<Dotfile>>(file.readText())
                    val dotFiles = FileUtils.listFiles(
                        collectionsPath.resolve(fileName).toFile(),
                        TrueFileFilter.INSTANCE,
                        null
                    ) // non-recursive as there should not be folders in the collection folder

                    for (metadata in dotfilesMetadata) {
                        val metadataPathname = metadata.pathname.expandTildeWithHomePathname()
                        val localPath = Path(metadataPathname).toLocal()

                        if (localPath.exists()) {
                            echos.echoWarning(
                                "$metadataPathname in local state is prioritised over the one in Punkt Hub collection. Therefore, skipping activation of this dotfile.",
                                globalOptions.verbosity,
                                Verbosity.NORMAL
                            )
                            continue
                        }

                        FileUtils.createParentDirectories(File(metadataPathname))
                        FileUtils.copyFile(
                            dotFiles.first { it.name == metadata.fileName },
                            File(metadataPathname),
                            StandardCopyOption.REPLACE_EXISTING
                        )
                    }
                }
            }
        }
    }
}