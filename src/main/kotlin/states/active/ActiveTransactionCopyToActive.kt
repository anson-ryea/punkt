package com.an5on.states.active

import com.an5on.file.FileUtils.toActive
import org.apache.commons.io.FileUtils
import java.nio.file.Path
import kotlin.io.path.exists

/**
 * A transaction that copies a file from the local path to the active path.
 *
 * This transaction executes the copy operation when run.
 *
 * @param localPath the local path of the file to copy
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class ActiveTransactionCopyToActive(
    localPath: Path,
) : ActiveTransaction(
    ActiveTransactionType.COPY_TO_ACTIVE,
    localPath
) {
    /**
     * Copies a file from the local path to the corresponding active path.
     */
    override fun run() {
        assert(localPath.isAbsolute && localPath.exists())

        val localFile = localPath.toFile()
        val activeFile = localFile.toActive()

        FileUtils.copyFile(localFile, activeFile, java.nio.file.StandardCopyOption.REPLACE_EXISTING)
    }
}