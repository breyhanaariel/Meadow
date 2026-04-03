package com.meadow.core.sync.engine

import java.security.MessageDigest

class SyncHasher<T>(
    private val serializer: SyncSerializer<T>
) {
    fun hash(item: T): String {
        val json = serializer.serialize(item)
        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest(json.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
