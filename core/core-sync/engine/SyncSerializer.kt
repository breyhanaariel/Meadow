package com.meadow.core.sync.engine

interface SyncSerializer<T> {
    fun serialize(item: T): String
    fun deserialize(json: String): T
}
