package com.an5on.states.local

import com.an5on.states.local.LocalState.makeDirs
import java.nio.file.Path

class LocalTransactionMakeDirectories(
    override val activePath: Path,
) : LocalTransaction() {
    override val type = LocalTransactionType.MKDIRS

    override fun run() = makeDirs(activePath)
}