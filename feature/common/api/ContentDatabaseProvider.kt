package com.meadow.feature.common.api

interface ContentDatabaseProvider<T : Any> {
    val database: T
}
