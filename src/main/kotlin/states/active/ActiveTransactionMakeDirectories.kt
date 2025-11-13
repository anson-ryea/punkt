package com.an5on.states.active

import com.an5on.file.FileUtils.toActive
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * A transaction that creates the necessary directories for the active path corresponding to the local path.
 *
 * This transaction executes the directory creation operation when run.
 *
 * @param localPath the local path for which to create active directories
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class ActiveTransactionMakeDirectories(
    override val localPath: Path
) : ActiveTransaction() {
    override val type = ActiveTransactionType.MKDIRS

    /**
     * Creates the necessary directories for the active path corresponding to the local path.
     *
     */
    override fun run() {
        val activePath = localPath.toActive()

        if (activePath.isDirectory() && !activePath.exists()) {
            Files.createDirectories(activePath)
        } else if (!activePath.parent.exists()) {
            Files.createDirectories(activePath.parent)
        }
    }
}