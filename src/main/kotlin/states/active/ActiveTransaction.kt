package com.an5on.states.active

import arrow.core.Either
import com.an5on.error.PunktError
import java.nio.file.Path

abstract class ActiveTransaction {
    abstract val type: ActiveTransactionType
    abstract val localPath: Path

    abstract fun run(): Either<PunktError, Unit>

    override fun equals(other: Any?): Boolean = if (other is ActiveTransaction) {
        this.type == other.type && this.localPath == other.localPath
    } else {
        false
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + localPath.hashCode()
        return result
    }
}