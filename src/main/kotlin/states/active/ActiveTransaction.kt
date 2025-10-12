package com.an5on.states.active

import java.nio.file.Path

abstract class ActiveTransaction {
    abstract val type: ActiveTransactionType
    abstract val localPath: Path

    abstract fun run()

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