package com.an5on.states.tracked

import arrow.core.Either
import arrow.core.raise.either
import com.an5on.config.ActiveConfiguration
import com.an5on.error.PunktError
import com.an5on.error.TrackedError
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import org.h2.mvstore.MVMap
import org.h2.mvstore.MVStore
import java.nio.file.Path
import kotlin.io.path.exists

@OptIn(ExperimentalSerializationApi::class)
object TrackedEntriesStore {
    private var trackedStore: MVStore? = null
    private var trackedEntriesMap: MVMap<String, ByteArray>? = null

    private fun openStore(): Either<PunktError, Unit> = either {
        try {
            if (!ActiveConfiguration.trackedDbAbsPath.exists()) {
                ActiveConfiguration.trackedDbAbsPath.parent.toFile().mkdirs()
            }

            val store = MVStore.open(ActiveConfiguration.trackedDbAbsPathname)
            trackedStore = store
        } catch (e: Exception) {
            throw e
        }
    }

    private fun openMap(): Either<PunktError, Unit> = either {
        try {
            val store = trackedStore ?: raise(TrackedError.ConnectFailed(ActiveConfiguration.trackedDbAbsPath))
            val map = store.openMap<String, ByteArray>("trackedEntriesMap")
            trackedEntriesMap = map
        } catch (e: Exception) {
            throw e
        }
    }

    fun connect(): Either<PunktError, Unit> = either {
        openStore().bind()
        openMap().bind()
    }

    operator fun get(key: Path): TrackedEntry? = trackedEntriesMap!![key.toString()]?.let {
        Cbor.decodeFromByteArray(TrackedEntry.serializer(), it)
    }

    operator fun set(key: Path, value: TrackedEntry) {
        trackedEntriesMap!![key.toString()] = Cbor.encodeToByteArray(TrackedEntry.serializer(), value)
    }

    fun disconnect(): Either<PunktError, Unit> = either {
        trackedStore!!.close()
    }
}