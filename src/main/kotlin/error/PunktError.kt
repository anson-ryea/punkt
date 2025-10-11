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
}