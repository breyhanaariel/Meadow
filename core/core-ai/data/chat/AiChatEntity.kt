package com.meadow.core.ai.data.chat

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "ai_chats",
    indices = [
        Index(value = ["scopeKey", "personaKey"]),
        Index(value = ["updatedAtUtcMs"])
    ]
)
data class AiChatEntity(
    @PrimaryKey val id: String,
    val scopeKey: String,
    val personaKey: String,
    val name: String,
    val createdAtUtcMs: Long,
    val updatedAtUtcMs: Long,
    val remoteId: String? = null,
    val syncState: SyncState = SyncState.DIRTY
)

@Entity(
    tableName = "ai_messages",
    indices = [
        Index(value = ["chatId"]),
        Index(value = ["createdAtUtcMs"])
    ]
)
data class AiMessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val role: String,
    val content: String,
    val createdAtUtcMs: Long
)

