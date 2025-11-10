package com.an5on.states.local

import com.an5on.states.local.LocalUtils.toLocal
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * A transaction that creates the necessary directories for the local path corresponding to the active path.
 *
 * This transaction executes the directory creation operation when run.
 *
 * @param activePath the active path for which to create local directories
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class LocalTransactionKeepDirectory(
    override val activePath: Path,
) : LocalTransaction() {
    override val type = LocalTransactionType.KEEP_DIRECTORY

    override fun run() {
        assert(activePath.toFile().list().isEmpty())

        val localPath = activePath.toLocal()
        val localKeepFilePath = localPath.resolve(".punktkeep")
        if (activePath.isDirectory() && (!localPath.exists() || !localKeepFilePath.exists())) {
            Files.createDirectories(localPath)
            Files.createFile(localKeepFilePath)
        }
    }
}