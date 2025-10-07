package com.an5on.states.local

import arrow.core.Either
import com.an5on.error.PunktError
import java.nio.file.Path

abstract class LocalTransaction {
    abstract val type: LocalTransactionType
    abstract val activePath: Path

    abstract fun run(): Either<PunktError, Unit>

    override fun equals(other: Any?): Boolean = if (other is LocalTransaction) {
        this.type == other.type && this.activePath == other.activePath
    } else {
        false
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + activePath.hashCode()
        return result
    }
}