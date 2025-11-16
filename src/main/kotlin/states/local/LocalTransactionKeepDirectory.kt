package com.an5on.states.local

import com.an5on.file.FileUtils.toLocal
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.isDirectory

/**
 * Represents a transaction to preserve an empty directory in the local state.
 *
 * This transaction ensures that an empty directory from the active working area is mirrored in the local `.punkt`
 * repository. It achieves this by creating a `.punktkeep` file within the corresponding local directory, a common
 * convention to track directories that would otherwise be ignored by version control systems like Git.
 *
 * @property activePath The path of the empty directory in the active area to be preserved in the local state.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class LocalTransactionKeepDirectory(
    activePath: Path
) : LocalTransaction(
    LocalTransactionType.KEEP_DIRECTORY,
    activePath
) {
    /**
     * Executes the transaction to create the directory and its `.punktkeep` file in the local state.
     *
     * This method first asserts that the directory at [activePath] is empty. It then creates the corresponding
     * directory in the local state, along with a `.punktkeep` file inside it, if the directory or the keep-file
     * does not already exist.
     */
    override fun run() {
        assert(activePath.toFile().list()!!.isEmpty())

        val localPath = activePath.toLocal()
        val localKeepFilePath = localPath.resolve(".punktkeep")
        if (activePath.isDirectory() && (!localPath.exists() || !localKeepFilePath.exists())) {
            Files.createDirectories(localPath)
            Files.createFile(localKeepFilePath)
        }
    }
}