package com.meadow.core.utils.compare

object DeepCompare {

    inline fun <reified T> areSame(old: T?, new: T?): Boolean {
        if (old == null || new == null) return false
        return old == new
    }

    fun <T : Any> diff(old: T, new: T): Map<String, Pair<Any?, Any?>> {
        val map = mutableMapOf<String, Pair<Any?, Any?>>()

        old::class.members
            .filter { it.parameters.size == 1 }
            .forEach { prop ->
                val oldValue = runCatching { prop.call(old) }.getOrNull()
                val newValue = runCatching { prop.call(new) }.getOrNull()

                if (oldValue != newValue) {
                    map[prop.name] = oldValue to newValue
                }
            }

        return map
    }
}