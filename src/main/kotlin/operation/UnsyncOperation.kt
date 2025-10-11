package com.an5on.operation

import arrow.core.raise.Raise
import arrow.core.raise.ensure
import com.an5on.command.Echos
import com.an5on.error.LocalError
import com.an5on.error.PunktError
import com.an5on.states.local.LocalState
import com.an5on.states.local.LocalTransactionDelete
import com.an5on.states.local.LocalUtils.existsInLocal
import com.an5on.states.local.LocalUtils.toLocal
import java.nio.file.Path

object UnsyncOperation {
    fun Raise<PunktError>.unsync(activePaths: Set<Path>, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        activePaths.forEach {
            ensure(it.existsInLocal()) {
                LocalError.LocalPathNotFound(it)
            }
        }

        val localPaths = activePaths.map { it.toLocal() }.toSet()

        commit(localPaths, echos)
    }

    fun commit(localPaths: Set<Path>, echos: Echos) {
        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        LocalState.commit()
    }
}