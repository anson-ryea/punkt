package com.an5on.states.local

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState.copyFileFromActiveToLocal
import java.nio.file.Path

class LocalTransactionCopyToLocal(
    override val activePath: Path
): LocalTransaction() {
    override val type = LocalTransactionType.COPY_TO_LOCAL

    override fun run(): Either<PunktError, Unit> = either {
        copyFileFromActiveToLocal(activePath).bind()
    }
}