package com.meadow.core.network.models

data class AiResponseDto(
    val candidates: List<AiCandidate> = emptyList()
) {
    data class AiCandidate(
        val content: AiContent
    )

    data class AiContent(
        val parts: List<AiPart>
    )

    data class AiPart(
        val text: String
    )
}