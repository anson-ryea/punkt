package com.an5on.error

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.toActive
import java.nio.file.Path

/**
 * Represents errors related to the local state of the application.
 * These errors are typically recoverable and indicate a problem with the user's local setup.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
sealed interface LocalError : PunktError {
    /**
     * An error indicating that the `punkt` local repository has not been found.
     * This typically means the user has not yet initialised the repository using `punkt init`.
     *
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class LocalNotFound(
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_NOT_FOUND"
        override val message: String
            get() = "punkt local repository not found: Have you initialised it with \"punkt init\" yet?"
    }

    /**
     * An error indicating that a specific path is not found in the local state.
     * This suggests the path has not been synchronised.
     *
     * @property path The path that was not found.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class LocalPathNotFound(
        val path: Path,
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_PATH_NOT_FOUND"
        override val message: String
            get() = "${path.toActive()}: Have you synced it with \"punkt sync ${path.toActive()}\" yet?"
    }

    /**
     * An error indicating that the local state has already been initialised.
     *
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class LocalAlreadyInitialised(
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_ALREADY_INITIALISED"
        override val message: String
            get() = "punkt is already initialised at: ${configuration.global.localStatePath}"
    }
}