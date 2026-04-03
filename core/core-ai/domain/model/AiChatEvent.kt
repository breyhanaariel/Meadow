package com.meadow.core.ai.domain.model


sealed class AiChatEvent {
    data class PersonaChanged(val persona: AiPersona) : AiChatEvent()

    data class InputChanged(val text: String) : AiChatEvent()

    object SendPressed : AiChatEvent()

    object ClearResponse : AiChatEvent()

    data class NewChatClicked(val persona: AiPersona) : AiChatEvent()
    data class NewChatConfirmed(val name: String) : AiChatEvent()
    object NewChatDialogDismissed : AiChatEvent()

    data class ChatSelected(val chatId: String) : AiChatEvent()

    object RenameChatClicked : AiChatEvent()
    object RenameChatDialogDismissed : AiChatEvent()
    data class RenameChatConfirmed(val name: String) : AiChatEvent()
    data class DeleteChatClicked(val chatId: String) : AiChatEvent()

    data class ToggleMeadowPersonaContexts(val enabled: Boolean) : AiChatEvent()
    data object ReindexMeadowPdfs : AiChatEvent()
    data class PdfDocumentSelected(val document: PdfDocument) : AiChatEvent()
    data class OpenPdfViewer(val document: PdfDocument) : AiChatEvent()
    object ClosePdfViewer : AiChatEvent()
    data class OpenPdfAtPage(val documentPath: String, val page: Int) : AiChatEvent()
}
