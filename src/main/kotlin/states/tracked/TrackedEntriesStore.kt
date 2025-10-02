package com.an5on.states.tracked

import com.an5on.config.Configuration.trackedDbAbsPath
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import org.h2.mvstore.MVStore
import java.nio.file.Path

@OptIn(ExperimentalSerializationApi::class)
object TrackedEntriesStore {
    private var trackedStore: MVStore? = null
    private var trackedEntriesMap: MutableMap<String, ByteArray>? = null

    fun connect() {
        trackedStore = MVStore.open(trackedDbAbsPath)
        trackedEntriesMap = trackedStore!!.openMap("trackedEntriesMap")
    }

    fun get(key: Path): TrackedEntry? = trackedEntriesMap!![key.toString()]?.let {
        Cbor.decodeFromByteArray<TrackedEntry>(TrackedEntry.serializer(), it)
    }

    fun put(key: Path, value: TrackedEntry) {
        trackedEntriesMap!![key.toString()] = Cbor.encodeToByteArray(TrackedEntry.serializer(), value)
    }

    fun disconnect() {
        trackedStore!!.close()
    }
}