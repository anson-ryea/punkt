package com.an5on.states.local

import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.toLocal
import com.an5on.type.Verbosity
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * Manages the local state of files in the Punkt system.
 *
 * This object handles operations related to the local state, such as committing transactions and manipulating files.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object LocalState {
    /** Checks if the local Punkt repository already exists.
     *
     * @return `true` if the local Punkt repository exists, `false` otherwise.
     */
    fun exists() = configuration.global.localStatePath.exists()

    /**
     * A set of pending transactions to be committed.
     */
    val pendingTransactions = mutableSetOf<LocalTransaction>()

    fun echoPendingTransactions(verbosity: Verbosity, echos: Echos) {
        echos.echoWithVerbosity(
            "The following operations will be performed:".indented(),
            true,
            false,
            verbosity,
            Verbosity.FULL
        )

        pendingTransactions.forEach { transaction ->
            echos.echoWithVerbosity(
                "${transaction.type} - ${transaction.activePath}".indented(),
                true,
                false,
                verbosity,
                Verbosity.FULL
            )
        }
    }

    /**
     * Executes all pending transactions.
     */
    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }

    /**
     * Creates the necessary directories for the local path corresponding to the active path.
     */
    fun makeDirs(activePath: Path) {
        val localPath = activePath.toLocal()

        if (activePath.isDirectory() && !localPath.exists()) {
            Files.createDirectories(localPath)
        } else if (!localPath.parent.exists()) {
            Files.createDirectories(localPath.parent)
        }
    }
}