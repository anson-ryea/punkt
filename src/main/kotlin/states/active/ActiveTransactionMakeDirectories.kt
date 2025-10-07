package com.an5on.states.active

import com.an5on.error.PunktError
import arrow.core.Either
import arrow.core.raise.either
import com.an5on.states.active.ActiveState.makeDirs
import java.nio.file.Path

class ActiveTransactionMakeDirectories(
    override val localPath: Path
): ActiveTransaction() {
    override val type = ActiveTransactionType.MKDIRS

    override fun run(): Either<PunktError, Unit> = either {
        makeDirs(localPath).bind()
    }
}