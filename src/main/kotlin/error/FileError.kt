package com.an5on.error

import java.nio.file.Path

/** Filesystem-related domain errors. */
sealed interface FileError : PunktError {
    override val code: String

    data class PathNotFound(
        val path: Path,
        override val cause: Throwable? = null
    ) : FileError {
        override val code: String = "FILE_PATH_NOT_FOUND"
        override val message: String
            get() = "Path not found: $path"
    }

    data class NotADirectory(
        val path: String,
        override val cause: Throwable? = null
    ) : FileError {
        override val code: String = "FILE_NOT_A_DIRECTORY"
        override val message: String
            get() = "Not a directory: $path"
    }

    data class AlreadyExists(
        val path: String,
        override val cause: Throwable? = null
    ) : FileError {
        override val code: String = "FILE_ALREADY_EXISTS"
        override val message: String
            get() = "Already exists: $path"
    }

    data class PermissionDenied(
        val path: String,
        val operation: String,
        override val cause: Throwable? = null
    ) : FileError {
        override val code: String = "FILE_PERMISSION_DENIED"
        override val message: String
            get() = "Permission denied for $operation: $path"
    }

    data class IoFailure(
        val path: String,
        val operation: String,
        override val cause: Throwable? = null
    ) : FileError {
        override val code: String = "FILE_IO_FAILURE"
        override val message: String
            get() = "I/O failure during $operation: $path"
    }
}
