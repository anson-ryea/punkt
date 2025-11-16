package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.git.AddOperation
import com.an5on.git.CommitOperation
import com.an5on.git.CommitOperation.Companion.substituteCommitMessage
import com.an5on.git.PushOperation

/**
 * An interface representing a command operation that can be executed.
 *
 * This interface defines a standard structure for operations, consisting of optional `runBefore` and `runAfter`
 * steps that wrap a central `operate` method. The `run` method orchestrates this execution flow, ensuring
 * that each step is executed in sequence and short-circuiting if any step results in an error.
 *
 * This pattern is useful for encapsulating business logic with setup and teardown phases.
 *
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
interface Operable {
    /**
     * A hook that runs before the main [operate] method.
     *
     * This can be overridden to perform setup tasks, such as validation or resource initialisation.
     * By default, it does nothing and returns a successful result.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    fun runBefore(): Either<PunktError, Unit> = Either.Right(Unit)

    /**
     * The core logic of the operation.
     *
     * This method must be implemented by concrete classes to define the primary action of the operation.
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
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    fun runAfter(): Either<PunktError, Unit> = Either.Right(Unit)

    /**
     * Executes the full lifecycle of the operation: [runBefore], [operate], and [runAfter].
     *
     * The execution is wrapped in an `either` block, which ensures that the sequence is aborted
     * as soon as any step returns a `Left` (an error).
     *
     * @return An [Either] containing the first [PunktError] encountered, or [Unit] if all steps succeed.
     */
    fun run(): Either<PunktError, Unit> = either {
        runBefore().bind()
        operate().bind()
        runAfter().bind()
    }

    /**
     * A companion object containing utility functions related to operations.
     */
    companion object {
        /**
         * Executes a sequence of Git actions (add, commit, push) based on the `gitOnLocalChange` configuration.
         *
         * This function reads the `gitOnLocalChange` setting and performs the corresponding Git operations on the
         * local repository. The behaviour is determined by the ordinal value of the enum, allowing for combined
         * actions like "add and commit".
         *
         * - `NONE`: No action is taken.
         * - `ADD`: Stages all changes.
         * - `COMMIT`: Creates a commit (implies `ADD`).
         * - `PUSH`: Pushes changes (implies `ADD` and `COMMIT`).
         *
         * @param globalOptions The global command-line options, which contain the `gitOnLocalChange` setting.
         * @param operation The operation that triggered this Git action, used for generating the commit message.
         * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
         */
        fun executeGitOnLocalChange(globalOptions: GlobalOptions, operation: Operable) = either {
            val operationName = operation.javaClass.simpleName
                .replace("Operation", "")
                .lowercase()
            val ordinal = globalOptions.gitOnLocalChange.ordinal

            if (ordinal == 0) {
                return@either
            }
            if (ordinal % 2 == 1) {
                AddOperation(
                    globalOptions.useBundledGit,
                    targetPath = configuration.global.localStatePath
                ).operate().bind()
            }
            if (ordinal >= 2) {
                CommitOperation(
                    globalOptions.useBundledGit,
                    message = substituteCommitMessage(globalOptions.gitCommitMessage, operationName)
                ).operate().bind()
            }
            if (ordinal >= 4) {
                PushOperation(
                    globalOptions.useBundledGit,
                    force = false
                ).operate().bind()
            }
        }
    }
}