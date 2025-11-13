package com.an5on.error

import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.file.FileUtils.toActive
import java.nio.file.Path

sealed interface LocalError : PunktError {
    override val code: String

    data class LocalNotFound(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "LOCAL_NOT_FOUND"
        override val message: String
            get() = "punkt local repository not found: Have you initialised it with \"punkt init\" yet?"
    }

    data class LocalPathNotFound(
        val path: Path,
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_PATH_NOT_FOUND"
        override val message: String
            get() = "${path.toActive()}: Have you synced it with \"punkt sync ${path.toActive()}\" yet?"
    }

    data class LocalAlreadyInitialised(
        override val cause: Throwable? = null
    ) : LocalError {
        override val code: String = "LOCAL_ALREADY_INITIALISED"
        override val message: String
            get() = "punkt is already initialised at: ${configuration.global.localStatePath}"
    }
}