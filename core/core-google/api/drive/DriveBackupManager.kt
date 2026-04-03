package com.meadow.core.google.api.drive

import com.meadow.core.google.api.drive.internal.DriveBackupRepository
import javax.inject.Inject

class DriveBackupManager @Inject constructor(
    private val repo: DriveBackupRepository
) : DriveBackupContract {

    override suspend fun backupAll(): DriveUploadResult {
        return DriveUploadResult.Success
    }

    override suspend fun restoreLatest(): Boolean {
        return repo.restoreLatest() != null
    }
}
