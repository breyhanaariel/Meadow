package com.meadow.core.database

interface LocalRepositoryContract<T> {
    suspend fun getAll(): List<T>

    suspend fun replaceAll(newData: List<T>)

    suspend fun upsert(item: T)

    suspend fun upsertAll(items: List<T>)

    suspend fun deleteById(id: String)
}
