package com.meadow.core.ai.data.sync

data class SyncedChatDto(
    val id: String,
    val remoteId: String?,
    val scopeKey: String,
    val personaKey: String,
    val name: String,
    val updatedAtUtcMs: Long,
    val messages: List<SyncedMessageDto>
)

data class SyncedMessageDto(
    val id: String,
    val role: String,
    val content: String,
    val createdAtUtcMs: Long
)
