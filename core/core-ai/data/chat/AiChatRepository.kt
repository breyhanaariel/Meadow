package com.meadow.core.ai.data.chat

import com.meadow.core.ai.data.sync.SyncedChatDto
import com.meadow.core.ai.data.sync.SyncedMessageDto
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AiChatRepository @Inject constructor(
    private val dao: AiChatDao
) {

    companion object {
        const val MAX_MESSAGES_PER_CHAT = 30
    }

    suspend fun listChats(scopeKey: String, personaKey: String) =
        dao.listChats(scopeKey, personaKey)
            .map { AiChatSummary(it.id, it.name) }

    suspend fun createChat(scopeKey: String, personaKey: String, name: String): AiChatSummary {
        val now = System.currentTimeMillis()
        val id = UUID.randomUUID().toString()

        dao.upsertChat(
            AiChatEntity(
                id = id,
                scopeKey = scopeKey,
                personaKey = personaKey,
                name = name,
                createdAtUtcMs = now,
                updatedAtUtcMs = now,
                syncState = SyncState.DIRTY
            )
        )

        return AiChatSummary(id, name)
    }

    suspend fun renameChat(chatId: String, newName: String) {
        dao.renameChat(chatId, newName, System.currentTimeMillis())
    }

    suspend fun hardDeleteChat(chatId: String) {
        dao.deleteMessagesForChat(chatId)
        dao.deleteChat(chatId)
    }

    suspend fun listMessages(chatId: String) =
        dao.listMessages(chatId)
            .map { AiChatMessage(it.content, it.role == "user") }

    suspend fun appendUser(chatId: String, text: String) =
        append(chatId, "user", text)

    suspend fun appendAssistant(chatId: String, text: String) =
        append(chatId, "assistant", text)

    private suspend fun append(chatId: String, role: String, text: String) {
        val now = System.currentTimeMillis()

        dao.insertMessage(
            AiMessageEntity(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                role = role,
                content = text,
                createdAtUtcMs = now
            )
        )

        dao.markChatDirty(chatId, now)
        dao.trimMessagesToMax(chatId, MAX_MESSAGES_PER_CHAT)
    }

    suspend fun getDirtyChats(scopeKey: String, personaKey: String): List<SyncedChatDto> {

        val chats = dao.listDirtyChats(scopeKey, personaKey)

        return chats.map { chat ->
            val messages = dao.listMessages(chat.id)

            SyncedChatDto(
                id = chat.id,
                remoteId = chat.remoteId,
                scopeKey = chat.scopeKey,
                personaKey = chat.personaKey,
                name = chat.name,
                updatedAtUtcMs = chat.updatedAtUtcMs,
                messages = messages.map {
                    SyncedMessageDto(
                        id = it.id,
                        role = it.role,
                        content = it.content,
                        createdAtUtcMs = it.createdAtUtcMs
                    )
                }
            )
        }
    }

    suspend fun markAsSynced(chatId: String, remoteId: String) {
        dao.markChatSynced(chatId, remoteId)
    }

    suspend fun upsertFromRemote(remote: SyncedChatDto) {

        dao.upsertChat(
            AiChatEntity(
                id = remote.id,
                scopeKey = remote.scopeKey,
                personaKey = remote.personaKey,
                name = remote.name,
                createdAtUtcMs = remote.updatedAtUtcMs,
                updatedAtUtcMs = remote.updatedAtUtcMs,
                remoteId = remote.remoteId,
                syncState = SyncState.SYNCED
            )
        )

        dao.upsertMessages(
            remote.messages.map {
                AiMessageEntity(
                    id = it.id,
                    chatId = remote.id,
                    role = it.role,
                    content = it.content,
                    createdAtUtcMs = it.createdAtUtcMs
                )
            }
        )
    }
}
