package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import arrow.core.raise.ensure
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalState.existsInLocal
import com.an5on.states.local.LocalState.toLocalPath
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.utils.Echos
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

        val localPaths = activePaths.map { it.toLocalPath().bind() }.toSet()

        unsyncLocal(localPaths, echos).bind()
    }

    fun unsyncLocal(localPaths: Set<Path>, echos: Echos): Either<PunktError, Unit> = either {

        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        LocalState.transact().bind()
    }
}