package com.an5on.states.local

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * A transaction that deletes the file or directory at the local path corresponding to the active path.
 *
 * This transaction executes the delete operation when run.
 *
 * @param activePath the active path corresponding to the local path to delete
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class LocalTransactionDelete(
    override val activePath: Path
) : LocalTransaction() {
    override val type = LocalTransactionType.REMOVE

    /**
     * Deletes the file or directory at the local path.
     */
    override fun run() {
        assert(activePath.exists())

        if (activePath.isDirectory()) {
            activePath.toFile().deleteRecursively()
        } else {
            Files.delete(activePath)
        }
    }
}