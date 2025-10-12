package com.an5on.states.active

import com.an5on.states.active.ActiveState.copyFromLocalToActive
import java.nio.file.Path

/**
 * A transaction that copies a file from the local path to the active path.
 *
 * This transaction executes the copy operation when run.
 *
 * @param localPath the local path of the file to copy
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class ActiveTransactionCopyToActive(
    override val localPath: Path,
) : ActiveTransaction() {
    override val type = ActiveTransactionType.COPY_TO_ACTIVE

    override fun run() = copyFromLocalToActive(localPath)
}