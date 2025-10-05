package com.an5on.error

sealed interface ConfigError : PunktError {
    override val code: String

    data class InvalidHomeDir(
        val path: String?,
        override val cause: Throwable? = null
    ) : ConfigError {
        override val code: String = "CONFIG_INVALID_HOME_DIR"
        override val message: String
            get() = "Invalid or missing home directory: ${path ?: "<null>"}"
    }

    data class InvalidLocalDir(
        val path: String,
        override val cause: Throwable? = null
    ) : ConfigError {
        override val code: String = "CONFIG_INVALID_LOCAL_DIR"
        override val message: String
            get() = "Invalid local data directory: $path"
    }

    data class SerializationFailed(
        val reason: String,
        override val cause: Throwable? = null
    ) : ConfigError {
        override val code: String = "CONFIG_SERIALIZATION_FAILED"
        override val message: String
            get() = "Failed to serialize/deserialize configuration: $reason"
    }
}
