package com.an5on.states.local

import com.an5on.file.FileUtils.toLocal
import org.apache.commons.io.FileUtils
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

/**
 * A transaction that copies a file from the active path to the local path.
 *
 * This transaction executes the copy operation when run.
 *
 * @param activePath the active path of the file to copy
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
class LocalTransactionCopyToLocal(
    override val activePath: Path
) : LocalTransaction() {
    override val type = LocalTransactionType.COPY_TO_LOCAL

    /**
     * Copies a file from the active path to the corresponding local path.
     */
    override fun run() {
        assert(activePath.isAbsolute && activePath.exists())

        val activeFile = activePath.toFile()
        val localFile = activeFile.toLocal()

        FileUtils.copyFile(activeFile, localFile, StandardCopyOption.REPLACE_EXISTING)
    }
}