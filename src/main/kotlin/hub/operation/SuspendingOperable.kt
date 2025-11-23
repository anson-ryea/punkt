package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError

/**
 * Contract for suspendable operations that consist of three distinct phases.
 *
 * Implementations can provide optional pre- and post-processing via [runBefore] and [runAfter],
 * with the main operation logic implemented in [operate]. Each phase can fail with a [PunktError],
 * in which case the remaining phases are skipped.
 *
 * @param B The type produced by [runBefore] and consumed by [operate].
 * @param O The type produced by [operate] and consumed by [runAfter].
 * @param A The final result type produced by [runAfter] and returned from [run].
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface SuspendingOperable <B, O, A> {
    /**
     * A hook that runs before the main [operate] method.
     *
     * This can be overridden to perform setup tasks, such as validation or resource initialisation.
     * By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [PunktError] on failure or `Unit` (cast to [B]) on success.
     *
     * @since 0.1.0
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun runBefore(): Either<PunktError, B> = Either.Right(Unit as B)

    /**
     * The core logic of the operation.
     *
     * This method must be implemented by concrete classes to define the primary action of the operation.
     *
     * @param fromBefore The value produced by [runBefore], typically containing initialised context.
     * @return An [Either] containing a [PunktError] on failure or the intermediate result [O] on success.
     *
     * @since 0.1.0
     */
    suspend fun operate(fromBefore: B): Either<PunktError, O>

    /**
     * A hook that runs after the main [operate] method has completed successfully.
     *
     * This can be overridden to perform clean-up or finalisation tasks. It will not be executed if [runBefore]
     * or [operate] fails. By default, it simply forwards the [operate] result as the final result.
     *
     * @param fromOperate The value produced by [operate], typically representing the operation outcome.
     * @return An [Either] containing a [PunktError] on failure or the final result [A] on success.
     *
     * @since 0.1.0
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun runAfter(fromOperate: O): Either<PunktError, A> = Either.Right(fromOperate as A)

    /**
     * Executes the full lifecycle of the operation: [runBefore], [operate], and [runAfter].
     *
     * The execution is wrapped in an [either] block, which ensures that the sequence is aborted
     * as soon as any step returns a `Left` (an error).
     *
     * @return An [Either] containing the first [PunktError] encountered, or the final result [A] if all steps succeed.
     *
     * @since 0.1.0
     */
    suspend fun run(): Either<PunktError, A> = either {
        runAfter(operate(runBefore().bind()).bind()).bind()
    }
}