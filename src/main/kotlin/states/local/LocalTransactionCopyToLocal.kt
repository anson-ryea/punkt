package com.an5on.states.local

import com.an5on.file.FileUtils.toLocal
import org.apache.commons.io.FileUtils
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.exists

/**
 * Represents a transaction that copies a file from the active working directory to the local state.
 *
 * This class encapsulates the operation of copying a file to the local `.punkt` repository. When executed, it takes the
 * specified [activePath], resolves its corresponding location in the local state, and performs the copy. If a file
 * with the same name already exists at the destination, it will be replaced.
 *
 * @property activePath The absolute [Path] of the file in the active directory to be copied.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
class LocalTransactionCopyToLocal(
    activePath: Path
) : LocalTransaction(
    LocalTransactionType.COPY_TO_LOCAL,
    activePath
) {
    /**
     * Executes the copy operation.
     *
     * This method copies the file from the [activePath] to its corresponding location in the local state.
     * It asserts that the [activePath] is an absolute path and that the file exists before attempting the copy.
     * The copy operation will replace any existing file at the destination.
     */
    override fun run() {
        assert(activePath.isAbsolute && activePath.exists())

        val activeFile = activePath.toFile()
        val localFile = activeFile.toLocal()

        FileUtils.copyFile(activeFile, localFile, StandardCopyOption.REPLACE_EXISTING)
    }
}