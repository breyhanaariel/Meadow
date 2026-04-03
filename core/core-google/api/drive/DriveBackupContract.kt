package com.meadow.core.google.api.drive

interface DriveBackupContract {

    suspend fun backupAll(): DriveUploadResult

    suspend fun restoreLatest(): Boolean
}
