package com.meadow.core.google.api.drive.internal

sealed class DriveUploadResult {
    data object Success : DriveUploadResult()
    data class Failure(val reason: String, val throwable: Throwable? = null) : DriveUploadResult()
}