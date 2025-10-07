package com.an5on.error

import com.an5on.states.active.ActiveState.toActivePath
import java.nio.file.Path

sealed interface LocalError : PunktError {
    override val code: String

    data class LocalNotFound(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "LOCAL_NOT_FOUND"
        override val message: String
            get() = "No local repository found. Run 'punkt init' to initialize a local repository."
    }

    data class LocalPathNotFound(
        val path: Path,
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_PATH_NOT_FOUND"
        override val message: String
            get() = "The local repository does not hold a copy of: ${path.toActivePath()}"
    }
}