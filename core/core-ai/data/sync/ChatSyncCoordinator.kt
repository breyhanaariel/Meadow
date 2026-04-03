package com.meadow.core.ai.data.sync

import com.meadow.core.ai.data.chat.AiChatRepository
import com.meadow.core.ai.data.remote.RemoteChatDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatSyncCoordinator @Inject constructor(
    private val chatRepo: AiChatRepository,
    private val remoteDataSource: RemoteChatDataSource
) {

    suspend fun syncAll(
        scopeKey: String,
        personaKey: String
    ) {

        val dirty = chatRepo.getDirtyChats(scopeKey, personaKey)

        dirty.forEach { chat ->
            val remoteId = remoteDataSource.upsertChat(chat)
            chatRepo.markAsSynced(chat.id, remoteId)
        }

        val remoteChats =
            remoteDataSource.fetchChats(scopeKey, personaKey)

        remoteChats.forEach { remote ->
            chatRepo.upsertFromRemote(remote)
        }
    }
}
