package com.an5on.states.local

import java.nio.file.Path

class LocalTransactionDelete(
    override val activePath: Path
) : LocalTransaction() {
    override val type = LocalTransactionType.REMOVE

    override fun run() = LocalState.delete(activePath)
}