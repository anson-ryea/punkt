package com.an5on.states.local

import com.an5on.states.local.LocalState.makeDirs
import java.nio.file.Path

/**
 * A transaction that creates the necessary directories for the local path corresponding to the active path.
 *
 * This transaction executes the directory creation operation when run.
 *
 * @param activePath the active path for which to create local directories
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class LocalTransactionMakeDirectories(
    override val activePath: Path,
) : LocalTransaction() {
    override val type = LocalTransactionType.MKDIRS

    override fun run() = makeDirs(activePath)
}