package com.an5on.error

/**
 * Root marker and contract for all domain errors in Punkt.
 *
 * Each error provides a stable [code], a human-friendly [message],
 * and an optional underlying [cause].
 */
interface PunktError {
    val code: String
    val message: String
    val cause: Throwable?
        get() = null
    val statusCode: Int
        get() = 1
}