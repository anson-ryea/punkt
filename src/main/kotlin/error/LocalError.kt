package com.an5on.error

sealed interface LocalError : PunktError {
    override val code: String

    data class LocalNotFound(
        override val cause: Throwable? = null
    ) : GitError {
        override val code: String = "LOCAL_NOT_FOUND"
        override val message: String
            get() = "No local repository found. Run 'punkt init' to initialize a local repository."
    }
}