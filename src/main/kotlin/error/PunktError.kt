package com.an5on.error

/**
 * Root marker and contract for all domain errors in Punkt.
 *
 * Each error provides a stable [code], a human-friendly [message],
 * and an optional underlying [cause].
 */
sealed interface PunktError {
    val code: String
    val message: String
    val statusCode: Int
        get() = 1
    val cause: Throwable?
        get() = null

    data class OperationCancelled(
        override val message: String = "Operation cancelled by user",
        override val cause: Throwable? = null
    ) : PunktError {
        override val code: String = "PUNKT_OPERATION_CANCELLED"
    }
}