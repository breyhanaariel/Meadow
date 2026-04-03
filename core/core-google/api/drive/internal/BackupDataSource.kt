package com.meadow.core.google.api.drive.internal

interface BackupDataSource {
    fun key(): String
    suspend fun exportData(): Any
    suspend fun importData(data: Any)
}
