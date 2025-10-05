package com.an5on.error

/** Filesystem-related domain errors. */
sealed interface FilesystemError : PunktError {
    override val code: String

    data class PathNotFound(
        val path: String,
        override val cause: Throwable? = null
    ) : FilesystemError {
        override val code: String = "FS_PATH_NOT_FOUND"
        override val message: String
            get() = "Path not found: $path"
    }

    data class NotADirectory(
        val path: String,
        override val cause: Throwable? = null
    ) : FilesystemError {
        override val code: String = "FS_NOT_A_DIRECTORY"
        override val message: String
            get() = "Not a directory: $path"
    }

    data class AlreadyExists(
        val path: String,
        override val cause: Throwable? = null
    ) : FilesystemError {
        override val code: String = "FS_ALREADY_EXISTS"
        override val message: String
            get() = "Already exists: $path"
    }

    data class PermissionDenied(
        val path: String,
        val operation: String,
        override val cause: Throwable? = null
    ) : FilesystemError {
        override val code: String = "FS_PERMISSION_DENIED"
        override val message: String
            get() = "Permission denied for $operation: $path"
    }

    data class IoFailure(
        val path: String,
        val operation: String,
        override val cause: Throwable? = null
    ) : FilesystemError {
        override val code: String = "FS_IO_FAILURE"
        override val message: String
            get() = "I/O failure during $operation: $path"
    }
}
