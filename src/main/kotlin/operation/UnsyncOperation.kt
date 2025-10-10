package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.command.Echos
import java.nio.file.Path

object UnsyncOperation {
    fun unsync(activePaths: Set<Path>, echos: Echos): Either<PunktError, Unit> = either {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        activePaths.forEach {
            ensure(it.existsInLocal().bind()) {
                LocalError.LocalPathNotFound(it)
            }
        }

        val localPaths = activePaths.map { it.toLocal().bind() }.toSet()

        commit(localPaths, echos).bind()
    }

    fun commit(localPaths: Set<Path>, echos: Echos): Either<PunktError, Unit> = either {
        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        LocalState.commit().bind()
    }
}