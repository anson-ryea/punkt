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

/**
 * Handles the unsync operation to remove files from the local state.
 *
 * This object provides operations to unsync paths by deleting them from the local state.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object UnsyncOperation {
    /**
     * Unsynchronizes the specified active paths by removing them from the local state.
     *
     * @param activePaths the set of active paths to unsync
     * @param echos the echo functions for output
     */
    fun Raise<PunktError>.unsync(activePaths: Set<Path>, echos: Echos) {
        ensure(LocalState.exists()) {
            LocalError.LocalNotFound()
        }

        val localPaths = activePaths.map { activePath ->
            ensure(activePath.existsInLocal()) {
                LocalError.LocalPathNotFound(activePath)
            }

            activePath.toLocal()
        }.toSet()

        commit(localPaths, echos)
    }

    private fun commit(localPaths: Set<Path>, echos: Echos) {
        LocalState.pendingTransactions.addAll(
            localPaths.map { LocalTransactionDelete(it) }
        )

        LocalState.commit()
    }
}