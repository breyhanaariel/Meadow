package com.meadow.core.ai.engine.memory

import androidx.datastore.core.Serializer
import java.io.InputStream
import java.io.OutputStream

object StringDataStoreSerializer : Serializer<String> {

    override val defaultValue: String = "{}"

    override suspend fun readFrom(input: InputStream): String {
        return input.readBytes().decodeToString()
    }

    override suspend fun writeTo(t: String, output: OutputStream) {
        output.write(t.encodeToByteArray())
    }
}
