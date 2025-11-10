package com.an5on.states.active

import com.an5on.command.CommandUtils.indented
import com.an5on.command.Echos
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.type.Verbosity
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * Manages the files in active state in the Punkt system.
 *
 * This object handles transactions and operations related to the active state, such as copying files and creating directories.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object ActiveState {
    /**
     * A set of pending transactions to be executed.
     */
    val pendingTransactions = mutableSetOf<ActiveTransaction>()

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
                "${transaction.type} - ${transaction.localPath}".indented(),
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
     * Copies a file from the local path to the corresponding active path.
     *
     * @param localPath the absolute path of the local file to copy
     */
    fun copyFromLocalToActive(localPath: Path) {
        assert(localPath.isAbsolute && localPath.exists())

        val localFile = localPath.toFile()
        val activeFile = localFile.toActive()

        FileUtils.copyFile(localFile, activeFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Creates the necessary directories for the active path corresponding to the local path.
     *
     * @param localPath the local path for which to create active directories
     */
    fun makeDirs(localPath: Path) {
        val activePath = localPath.toActive()

        if (activePath.isDirectory() && !activePath.exists()) {
            Files.createDirectories(activePath)
        } else if (!activePath.parent.exists()) {
            Files.createDirectories(activePath.parent)
        }
    }
}