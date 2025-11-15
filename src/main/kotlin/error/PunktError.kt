package com.an5on.error

/**
 * A sealed interface representing the root of all domain-specific errors within the `punkt` application.
 *
 * This interface provides a common contract for all custom errors, ensuring they include a stable machine-readable [code],
 * a human-friendly [message], a process exit [statusCode], and an optional underlying [cause].
 *
 * @property code A unique, stable identifier for the error type.
 * @property message A descriptive, human-readable message explaining the error.
 * @property statusCode The exit code to be used when the application terminates due to this error. Defaults to 1.
 * @property cause The optional underlying `Throwable` that caused this error.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
sealed interface PunktError {
    val code: String
    val message: String
    val statusCode: Int
        get() = 1
    val cause: Throwable?
        get() = null

    /**
     * An error indicating that the current operation was cancelled by the user.
     *
     * @property message A message indicating that the operation was cancelled.
     * @property cause The underlying cause of the cancellation, if any.
     * @since 0.1.0
     * @author Anson Ng <hej@an5on.com>
     */
    data class OperationCancelled(
        override val message: String = "Operation cancelled by user",
        override val cause: Throwable? = null
    ) : PunktError {
        override val code: String = "PUNKT_OPERATION_CANCELLED"
    }
}