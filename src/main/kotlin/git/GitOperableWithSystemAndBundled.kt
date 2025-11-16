package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.PunktError
import com.an5on.type.BooleanWithAuto

/**
 * An abstract base class for Git operations that can be executed using either the bundled JGit library or the
 * system's native Git executable.
 *
 * This class provides a unified entry point for Git operations by implementing both [GitOperableWithBundled] and
 * [GitOperableWithSystem]. It determines which implementation to use based on the `useBundledGit` flag, which
 * is typically resolved from user configuration or command-line options.
 *
 * @property useBundledGit A boolean flag that determines the execution strategy. If `true`, the operation will be
 * performed using the bundled JGit library. If `false`, it will delegate to the system's native Git executable.
 * @since 0.1.0
 * @author Anson Ng <hej@an5on.com>
 */
abstract class GitOperableWithSystemAndBundled(
    protected val useBundledGit: Boolean
) : GitOperableWithBundled, GitOperableWithSystem {
    /**
     * Executes the Git operation using the selected strategy (bundled or system).
     *
     * This method checks the `useBundledGit` flag and calls either [operateWithBundled] or [operateWithSystem]
     * accordingly. It ensures that the operation is performed through the correct implementation path.
     *
     * @return An [Either] containing a [PunktError] on failure or [Unit] on success.
     */
    override fun operate(): Either<PunktError, Unit> = either {
        if (useBundledGit) {
            operateWithBundled().bind()
        } else {
            operateWithSystem().bind()
        }
    }

    /**
     * A companion object containing utility functions for determining the Git execution strategy.
     */
    companion object {
        /**
         * A property that checks if a native Git executable is installed and available in the system's PATH.
         *
         * It works by attempting to run `git --version`. A successful execution (exit code 0) indicates that
         * Git is installed. The result is not cached and is re-evaluated on each access.
         */
        val isGitInstalled: Boolean
            get() = try {
                val process = ProcessBuilder("git", "--version")
                    .redirectErrorStream(true)
                    .start()
                process.waitFor() == 0
            } catch (e: Exception) {
                false
            }

        /**
         * Determines whether to use the bundled Git implementation based on a command-line option and the global configuration.
         *
         * The logic is as follows:
         * 1. If the `useBundledGitOption` is explicitly `TRUE` or `FALSE`, that value is used.
         * 2. If the option is `AUTO`, it checks if a native Git executable is installed. If not, it falls back to the bundled Git.
         * 3. If the option is not provided (`null`), it falls back to the application's configuration (`configuration.git.useBundledGit`)
         *    and applies the same `TRUE`/`FALSE`/`AUTO` logic.
         *
         * @param useBundledGitOption The user-provided command-line option, which can be `TRUE`, `FALSE`, `AUTO`, or `null`.
         * @return `true` if the bundled Git should be used, `false` otherwise.
         */
        @JvmStatic
        protected fun determineUseBundledGit(useBundledGitOption: BooleanWithAuto?) = when (useBundledGitOption) {
            BooleanWithAuto.TRUE -> true
            BooleanWithAuto.FALSE -> false
            BooleanWithAuto.AUTO -> !isGitInstalled
            null -> {
                when (configuration.git.useBundledGit) {
                    BooleanWithAuto.TRUE -> true
                    BooleanWithAuto.FALSE -> false
                    BooleanWithAuto.AUTO -> !isGitInstalled
                }
            }
        }
    }
}