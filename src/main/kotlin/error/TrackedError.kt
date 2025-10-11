package com.an5on.error

import java.nio.file.Path

interface TrackedError : PunktError {
    override val code: String

    data class ConnectFailed(
        val path: Path,
        override val cause: Throwable? = null
    ) : TrackedError {
        override val code: String = "TRACKED_CONNECT_FAILED"
        override val message: String
            get() = "Failed to connect to tracked state database: $path"
    }
}