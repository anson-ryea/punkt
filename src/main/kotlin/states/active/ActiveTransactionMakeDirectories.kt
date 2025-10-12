package com.an5on.states.active

import com.an5on.states.active.ActiveState.makeDirs
import java.nio.file.Path

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

    override fun run() = makeDirs(localPath)
}