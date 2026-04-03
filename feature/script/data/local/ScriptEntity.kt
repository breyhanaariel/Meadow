package com.meadow.feature.script.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(
    tableName = "scripts",
    indices = [
        Index(value = ["parentType"]),
        Index(value = ["projectId"]),
        Index(value = ["seriesId"]),
        Index(value = ["seasonId"]),
        Index(value = ["updatedAt"]),
        Index(value = ["isDirty"])
    ]
)
data class ScriptEntity(
    @PrimaryKey
    val scriptId: String = UUID.randomUUID().toString(),

    val parentType: String,
    val projectId: String?,
    val seriesId: String?,

    val seasonId: String?,
    val type: String,
    val dialect: String,
    val canonicalLanguage: String,

    val localVersion: Long = 0L,
    val remoteVersion: Long = 0L,
    val localUpdatedAt: Long? = null,
    val remoteUpdatedAt: Long? = null,
    val lastFirestoreSyncAt: Long? = null,

    val isDirty: Int = 0,
    val lastSyncError: String? = null,

    val createdAt: Long,
    val updatedAt: Long
)
