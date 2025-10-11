package com.an5on.states.local

import com.an5on.states.local.LocalState.copyFileFromActiveToLocal
import java.nio.file.Path

class LocalTransactionCopyToLocal(
    override val activePath: Path
) : LocalTransaction() {
    override val type = LocalTransactionType.COPY_TO_LOCAL

    override fun run() = copyFileFromActiveToLocal(activePath)
}