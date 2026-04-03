package com.meadow.core.ai.data.chat

data class AiChatSummary(
    val id: String,
    val name: String
)

data class AiChatMessage(
    val content: String,
    val isUser: Boolean
)
