package com.an5on.hub.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.PunktError

interface SuspendingOperable <B, O, A> {
    /**
     * A hook that runs before the main [operate] method.
     *
     * This can be overridden to perform setup tasks, such as validation or resource initialisation.
     * By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun runBefore(): Either<PunktError, B> = Either.Right(Unit as B)

    /**
     * The core logic of the operation.
     *
     * This method must be implemented by concrete classes to define the primary action of the operation.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    suspend fun operate(fromBefore: B): Either<PunktError, O>

    /**
     * A hook that runs after the main [operate] method has completed successfully.
     *
     * This can be overridden to perform cleanup or finalisation tasks. It will not be executed if [runBefore]
     * or [operate] fails. By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    @Suppress("UNCHECKED_CAST")
    suspend fun runAfter(fromOperate: O): Either<PunktError, A> = Either.Right(fromOperate as A)

    /**
     * Executes the full lifecycle of the operation: [runBefore], [operate], and [runAfter].
     *
     * The execution is wrapped in an `either` block, which ensures that the sequence is aborted
     * as soon as any step returns a `Left` (an error).
     *
     * @return An [Either] containing the first [PunktError] encountered, or [Unit] if all steps succeed.
     */
    suspend fun run(): Either<PunktError, A> = either {
        runAfter(operate(runBefore().bind()).bind()).bind()
    }
}