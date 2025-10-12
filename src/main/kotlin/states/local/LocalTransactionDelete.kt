package com.an5on.states.local

import java.nio.file.Path

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

    override fun run() = LocalState.delete(activePath)
}