package com.an5on.states.local

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.FileUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
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

    /**
     * Executes all pending transactions.
     */
    fun commit() {
        pendingTransactions.forEach {
            it.run()
        }
    }

    /**
     * Copies a file from the active path to the corresponding local path.
     *
     * @param activePath the absolute path of the active file to copy
     */
    fun copyFileFromActiveToLocal(activePath: Path) {
        assert(activePath.isAbsolute && activePath.exists())

        val activeFile = activePath.toFile()
        val localFile = activeFile.toLocal()

        FileUtils.copyFile(activeFile, localFile, StandardCopyOption.REPLACE_EXISTING)
    }

    /**
     * Creates the necessary directories for the local path corresponding to the active path.
     *
     * @param activePath the active path for which to create local directories
     */
    fun makeDirs(activePath: Path) {
        val localPath = activePath.toLocal()

        if (activePath.isDirectory() && !localPath.exists()) {
            Files.createDirectories(localPath)
        } else if (!localPath.parent.exists()) {
            Files.createDirectories(localPath.parent)
        }
    }

    /**
     * Deletes the file or directory at the local path.
     *
     * @param localPath the local path to delete
     */
    fun delete(localPath: Path) {
        assert(localPath.exists())

        if (localPath.isDirectory()) {
            localPath.toFile().deleteRecursively()
        } else {
            Files.delete(localPath)
        }
    }
}