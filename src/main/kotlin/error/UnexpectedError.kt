package com.an5on.error

/** A generic, unexpected domain error wrapper to capture unknown failures. */
data class UnexpectedError(
    override val message: String,
    override val cause: Throwable? = null,
    val context: Map<String, String> = emptyMap()
) : PunktError {
    override val code: String = "UNEXPECTED"
}
