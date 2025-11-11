package com.an5on.operation

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.filter.DefaultLocalIgnoreFileFilter
import com.an5on.states.active.ActiveUtils.toActive
import com.an5on.states.local.LocalUtils.toLocal
import com.an5on.system.SystemUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.IOFileFilter
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
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded files and directories
     */
    fun File.expand(fileFilter: IOFileFilter, dirFilter: IOFileFilter? = fileFilter, filesOnly: Boolean = false) =
        if (!this.isDirectory()) {
            setOf(this).filter { fileFilter.accept(it) }.toSet()
        } else {
            FileUtils.listFilesAndDirs(
                this,
                fileFilter,
                dirFilter
            ).let {
                if (filesOnly) it.filter { file -> file.isFile } else it
            }.toSet()
        }

    /**
     * Expands this path into a set of paths based on the filter.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded paths
     */
    fun Path.expand(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ): Set<Path> =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath() }.toSet()

    /**
     * Expands this file or directory and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded local paths
     */
    fun File.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.expand(fileFilter, dirFilter, filesOnly).map { it.toLocal() }.toSet()

    /**
     * Expands this path and converts the results to local paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded local paths
     */
    fun Path.expandToLocal(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.toFile().expand(fileFilter, dirFilter, filesOnly).map { it.toPath().toLocal() }.toSet()

    /**
     * Expands this file or directory and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only files
     * @return the set of expanded active paths
     */
    fun File.expandToActive(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.expand(fileFilter, dirFilter, filesOnly)
            .map { it.toActive() }
            .filterNot { it.toPath() == SystemUtils.homePath }
            .toSet()

    /**
     * Expands this path and converts the results to active paths.
     *
     * @param recursive whether to expand recursively
     * @param fileFilter the filter to apply
     * @param filesOnly whether to include only paths
     * @return the set of expanded active paths
     */
    fun Path.expandToActive(
        fileFilter: IOFileFilter,
        dirFilter: IOFileFilter? = fileFilter,
        filesOnly: Boolean = false
    ) =
        this.toFile().expandToActive(fileFilter, dirFilter, filesOnly)
            .map { it.toPath() }
            .filterNot { it == SystemUtils.homePath }
            .toSet()

    /**
     * A set of active paths corresponding to all existing local paths.
     */
    val existingLocalPathsToActivePaths =
        configuration.global.localStatePath
            .expandToActive(DefaultLocalIgnoreFileFilter)
}