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

interface Operable {
    fun runBefore(): Either<PunktError, Unit> = Either.Right(Unit)

    fun operate(): Either<PunktError, Unit>

    fun runAfter(): Either<PunktError, Unit> = Either.Right(Unit)

    fun run(): Either<PunktError, Unit> = either {
        runBefore().bind()
        operate().bind()
        runAfter().bind()
    }

    companion object {
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