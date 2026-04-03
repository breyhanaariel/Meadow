package com.meadow.core.ai.domain.model

import com.meadow.core.ai.data.chat.AiChatSummary

data class AiChatUiState(
    val persona: AiPersona = AiPersona.Sprout,
    val input: String = "",
    val isLoading: Boolean = false,
    val chatSummaries: List<AiChatSummary> = emptyList(),
    val selectedChatId: String? = null,
    val selectedChatName: String? = null,
    val showNewChatDialog: Boolean = false,
    val showRenameChatDialog: Boolean = false,
    val includeMeadowPersonaContexts: Boolean = false,
    val context: AiContextPayload? = null,
    val contextLabel: String? = null,
    val pdfDocuments: List<PdfDocument> = emptyList(),
    val selectedPdf: PdfDocument? = null,
    val openViewer: Boolean = false,
    val pdfState: PdfState? = null

)
