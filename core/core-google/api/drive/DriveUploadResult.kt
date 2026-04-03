package com.meadow.core.google.api.drive

sealed class DriveUploadResult {
    data object Success : DriveUploadResult()
    data class Failure(val reason: String, val throwable: Throwable? = null) : DriveUploadResult()
}
