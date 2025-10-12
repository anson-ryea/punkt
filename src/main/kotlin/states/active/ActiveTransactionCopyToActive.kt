package com.an5on.states.active

import com.an5on.states.active.ActiveState.copyFromLocalToActive
import java.nio.file.Path

class ActiveTransactionCopyToActive(
    override val localPath: Path,
) : ActiveTransaction() {
    override val type = ActiveTransactionType.COPY_TO_ACTIVE

    override fun run() = copyFromLocalToActive(localPath)
}