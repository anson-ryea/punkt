package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.GitError
import com.an5on.error.PunktError

/**
 * An interface representing a Git operation that can be executed.
 *
 * This interface defines a standard structure for Git-related operations, consisting of optional `runBefore` and
 * `runAfter` steps that wrap a central `operate` method. The `run` method orchestrates this execution flow,
 * ensuring that each step is executed in sequence and short-circuiting if any step results in a Git error.
 *
 * This pattern is useful for encapsulating Git logic with setup and teardown phases.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface GitOperable {
    /**
     * A hook that runs before the main [operate] method.
     *
     * This can be overridden to perform setup tasks, such as validation or pre-flight checks.
     * By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    fun runBefore(): Either<GitError, Unit> = Either.Right(Unit)

    /**
     * The core logic of the Git operation.
     *
     * This method must be implemented by concrete classes to define the primary action of the operation,
     * such as `clone`, `pull`, or `commit`.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    fun operate(): Either<PunktError, Unit>

    /**
     * A hook that runs after the main [operate] method has completed successfully.
     *
     * This can be overridden to perform cleanup or finalisation tasks. It will not be executed if [runBefore]
     * or [operate] fails. By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [GitError] on failure or [Unit] on success.
     */
    fun runAfter(): Either<GitError, Unit> = Either.Right(Unit)

    /**
     * Executes the full lifecycle of the Git operation: [runBefore], [operate], and [runAfter].
     *
     * The execution is wrapped in an `either` block, which ensures that the sequence is aborted
     * as soon as any step returns a `Left` (a Git error).
     *
     * @return An [Either] containing the first [PunktError] encountered, or [Unit] if all steps succeed.
     */
    fun run(): Either<PunktError, Unit> = either {
        runBefore().bind()
        operate().bind()
        runAfter().bind()
    }
}