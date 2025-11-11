package com.an5on.operation

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.command.options.GlobalOptions
import com.an5on.config.ActiveConfiguration.configuration
import com.an5on.error.GitError
import com.an5on.error.PunktError
import com.an5on.git.AddOperation.add
import com.an5on.git.CommitOperation.commit
import com.an5on.git.GitUtils.substituteCommitMessage
import com.an5on.git.PushOperation.push

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
        fun executeGitOnLocalChange(globalOptions: GlobalOptions, operation: Operable) = either<GitError, Unit> {
            val operationName = operation.javaClass.simpleName.lowercase()
            val ordinal = globalOptions.gitOnLocalChange.ordinal

            if (ordinal == 0) {
                return Either.Right(Unit)
            }
            if (ordinal % 2 == 1) {
                add(
                    configuration.global.localStatePath,
                    globalOptions.useBundledGit
                )
            }
            if (ordinal >= 2) {
                commit(
                    substituteCommitMessage(globalOptions.gitCommitMessage, operationName),
                    globalOptions.useBundledGit
                )
            }
            if (ordinal >= 4) {
                push(false, globalOptions.useBundledGit)
            }
        }
    }
}