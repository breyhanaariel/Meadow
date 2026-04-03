package com.meadow.core.google.api.drive.model

data class DriveCreateFolderRequest(
    val name: String,
    val mimeType: String,
    val parents: List<String>? = null
)
