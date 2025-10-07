package com.an5on.states.active

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError
import com.an5on.states.active.ActiveState.copyFromLocalToActive
import java.nio.file.Path

class ActiveTransactionCopyToActive(
    override val localPath: Path,
) : ActiveTransaction() {
    override val type = ActiveTransactionType.COPY_TO_ACTIVE

    override fun run(): Either<PunktError, Unit> = either {
        copyFromLocalToActive(localPath).bind()
    }
}