package com.an5on.operation

import arrow.core.Either
import com.an5on.error.PunktError

interface Operable {
    fun runBefore(): Either<PunktError, Unit> = Either.Right(Unit)

    fun operate(): Either<PunktError, Unit>

    fun runAfter(): Either<PunktError, Unit> = Either.Right(Unit)
}