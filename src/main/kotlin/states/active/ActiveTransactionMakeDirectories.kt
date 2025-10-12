package com.an5on.states.active

import com.an5on.states.active.ActiveState.makeDirs
import java.nio.file.Path

class ActiveTransactionMakeDirectories(
    override val localPath: Path
) : ActiveTransaction() {
    override val type = ActiveTransactionType.MKDIRS

    override fun run() = makeDirs(localPath)
}