package com.meadow.core.ai.data.chat

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AiChatDao {

    @Query(
        """
        SELECT * FROM ai_chats
        WHERE scopeKey = :scopeKey AND personaKey = :personaKey
        ORDER BY updatedAtUtcMs DESC
        """
    )
    suspend fun listChats(scopeKey: String, personaKey: String): List<AiChatEntity>

    @Query("SELECT * FROM ai_chats WHERE id = :chatId LIMIT 1")
    suspend fun getChat(chatId: String): AiChatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChat(chat: AiChatEntity)

    @Query("UPDATE ai_chats SET name = :name, updatedAtUtcMs = :now WHERE id = :chatId")
    suspend fun renameChat(chatId: String, name: String, now: Long)

    @Query("DELETE FROM ai_chats WHERE id = :chatId")
    suspend fun deleteChat(chatId: String)

    @Query("DELETE FROM ai_messages WHERE chatId = :chatId")
    suspend fun deleteMessagesForChat(chatId: String)

    @Query(
        """
        SELECT * FROM ai_messages
        WHERE chatId = :chatId
        ORDER BY createdAtUtcMs ASC
        """
    )
    suspend fun listMessages(chatId: String): List<AiMessageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(msg: AiMessageEntity)

    @Query(
        """
        DELETE FROM ai_messages
        WHERE id IN (
            SELECT id FROM ai_messages
            WHERE chatId = :chatId
            ORDER BY createdAtUtcMs DESC
            LIMIT -1 OFFSET :maxMessages
        )
        """
    )
    suspend fun trimMessagesToMax(chatId: String, maxMessages: Int)

    @Query("""
    SELECT * FROM ai_chats
    WHERE scopeKey = :scopeKey
    AND personaKey = :personaKey
    AND syncState = 'DIRTY'
""")
    suspend fun listDirtyChats(
        scopeKey: String,
        personaKey: String
    ): List<AiChatEntity>

    @Query("""
    UPDATE ai_chats
    SET syncState = 'SYNCED',
        remoteId = :remoteId
    WHERE id = :chatId
""")
    suspend fun markChatSynced(
        chatId: String,
        remoteId: String
    )

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertMessages(messages: List<AiMessageEntity>)

    @Query("""
    UPDATE ai_chats
    SET syncState = 'DIRTY',
        updatedAtUtcMs = :updatedAt
    WHERE id = :chatId
""")
    suspend fun markChatDirty(
        chatId: String,
        updatedAt: Long
    )

}
