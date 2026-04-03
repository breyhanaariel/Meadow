package com.meadow.core.ai.data.remote

import com.meadow.core.ai.data.sync.SyncedChatDto

interface RemoteChatDataSource {

    suspend fun upsertChat(chat: SyncedChatDto): String

    suspend fun fetchChats(
        scopeKey: String,
        personaKey: String
    ): List<SyncedChatDto>
}
