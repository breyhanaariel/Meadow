package com.meadow.core.sync.engine

import com.google.gson.Gson

@Suppress("UNCHECKED_CAST")
class DefaultSyncSerializer(
    private val gson: Gson = Gson()
) : SyncSerializer<Any> {

    override fun serialize(item: Any): String =
        gson.toJson(item)

    override fun deserialize(json: String): Any =
        gson.fromJson(json, Any::class.java)
}
