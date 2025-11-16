package com.an5on.error

import java.nio.file.Path

/**
 * Represents errors related to the tracked state of the application.
 * These errors typically occur when there are issues with the underlying database that stores tracked file information.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
sealed interface TrackedError : PunktError {
    /**
     * An error indicating that a connection to the tracked state database could not be established.
     *
     * @property path The path to the database file.
     * @property cause The underlying cause of the error, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class ConnectFailed(
        val path: Path,
        override val cause: Throwable? = null
    ) : TrackedError {
        override val code: String = "TRACKED_CONNECT_FAILED"
        override val message: String
            get() = "Failed to connect to tracked state database: $path"
    }
}