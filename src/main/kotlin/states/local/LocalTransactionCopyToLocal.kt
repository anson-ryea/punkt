package com.an5on.states.local

import com.an5on.states.local.LocalState.copyFileFromActiveToLocal
import java.nio.file.Path

/**
 * A transaction that copies a file from the active path to the local path.
 *
 * This transaction executes the copy operation when run.
 *
 * @param activePath the active path of the file to copy
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class LocalTransactionCopyToLocal(
    override val activePath: Path
) : LocalTransaction() {
    override val type = LocalTransactionType.COPY_TO_LOCAL

    override fun run() = copyFileFromActiveToLocal(activePath)
}