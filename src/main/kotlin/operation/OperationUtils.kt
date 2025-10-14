package com.an5on.operation

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
import org.apache.commons.io.filefilter.TrueFileFilter
import java.io.File
import java.nio.file.Path

/**
 * Utility functions for operations on files and paths.
 *
 * This object provides extension functions to expand directories with filters and convert between local and active paths.
 *
 * @author Anson Ng <hej@an5on.com>
 * @since 0.1.0
 */
object OperationUtils {
    /**
     * Expands this file or directory into a set of files and directories based on the filter.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded files and directories
     */
    fun File.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        if (!this.isDirectory()) {
            setOf(this).filter { filter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                this,
                filter,
                if (recursive) filter else null
            ).let {
                if (filesOnly) it.filter { file -> file.isFile } else it
            }.toSet()
        }

    /**
     * Expands this path into a set of paths based on the filter.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded paths
     */
    fun Path.expand(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false): Set<Path> =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath() }.toSet()

    /**
     * Expands this file or directory and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded local paths
     */
    fun File.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.expand(recursive, filter, filesOnly).map { it.toLocal() }.toSet()

    /**
     * Expands this path and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded local paths
     */
    fun Path.expandToLocal(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath().toLocal() }.toSet()

    /**
     * Expands this file or directory and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded active paths
     */
    fun File.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.expand(recursive, filter, filesOnly).map { it.toActive() }.toSet()

    /**
     * Expands this path and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param filter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded active paths
     */
    fun Path.expandToActive(recursive: Boolean = true, filter: IOFileFilter, filesOnly: Boolean = false) =
        this.toFile().expand(recursive, filter, filesOnly).map { it.toPath().toActive() }.toSet()

    /**
     * A set of active paths corresponding to all existing local paths.
     */
    val existingLocalPathsToActivePaths = configuration.general.localStatePath.expandToActive(true, TrueFileFilter.INSTANCE)

}