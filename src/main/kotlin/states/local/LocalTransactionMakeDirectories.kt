package com.an5on.states.local

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState.makeDirs
import java.nio.file.Path

class LocalTransactionMakeDirectories(
    val relPath: Path,
): LocalTransaction {
    override val type: LocalTransactionType = LocalTransactionType.MKDIRS

    override fun run(): Either<PunktError, Unit> = either {
        makeDirs(relPath).bind()
    }
}