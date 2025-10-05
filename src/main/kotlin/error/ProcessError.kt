package com.an5on.error

/** Errors for starting and interacting with external processes. */
sealed interface ProcessError : PunktError {
    override val code: String

    data class StartFailed(
        val command: String,
        val args: List<String> = emptyList(),
        override val cause: Throwable
    ) : ProcessError {
        override val code: String = "PROC_START_FAILED"
        override val message: String
            get() = "Failed to start process: $command ${args.joinToString(" ")}".trim()
    }

    data class NonZeroExit(
        val command: String,
        val exitCode: Int,
        val stderr: String? = null
    ) : ProcessError {
        override val code: String = "PROC_NON_ZERO_EXIT"
        override val message: String
            get() = buildString {
                append("Process exited with code $exitCode: $command")
                if (!stderr.isNullOrBlank()) append(" | stderr: ").append(stderr)
            }
        override val cause: Throwable? = null
    }

    data class TimedOut(
        val command: String,
        val timeoutMs: Long
    ) : ProcessError {
        override val code: String = "PROC_TIMEOUT"
        override val message: String
            get() = "Process timed out after ${timeoutMs}ms: $command"
        override val cause: Throwable? = null
    }

    data class OutputParseFailed(
        val command: String,
        val reason: String,
        override val cause: Throwable? = null
    ) : ProcessError {
        override val code: String = "PROC_OUTPUT_PARSE_FAILED"
        override val message: String
            get() = "Failed to parse process output for '$command': $reason"
    }
}
