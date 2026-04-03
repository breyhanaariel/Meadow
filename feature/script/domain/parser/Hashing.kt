package com.meadow.feature.script.domain.parser

import java.security.MessageDigest

internal object Hashing {

    fun sha1(
        input: String
    ): String {
        val digest = MessageDigest.getInstance("SHA-1")
        val bytes = digest.digest(input.toByteArray(Charsets.UTF_8))
        return bytes.joinToString(separator = "") { b ->
            val v = b.toInt() and 0xff
            v.toString(16).padStart(2, '0')
        }
    }
}
