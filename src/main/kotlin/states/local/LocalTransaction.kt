package com.an5on.states.local

import arrow.core.Either
import com.an5on.error.PunktError

interface LocalTransaction {
    val type: LocalTransactionType

    fun run(): Either<PunktError, Unit>
}