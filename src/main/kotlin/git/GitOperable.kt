package com.an5on.git

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.error.GitError

interface GitOperable {
    fun runBefore(): Either<GitError, Unit> = Either.Right(Unit)

    fun operate(): Either<GitError, Unit>

    fun runAfter(): Either<GitError, Unit> = Either.Right(Unit)

    fun run(): Either<GitError, Unit> = either {
        runBefore().bind()
        operate().bind()
        runAfter().bind()
    }
}