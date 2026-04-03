package com.meadow.core.ai.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meadow.core.ai.engine.personas.meadow.MeadowUseCases
import com.meadow.core.ai.data.chat.AiChatMessage
import com.meadow.core.ai.data.chat.AiChatRepository
import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.domain.contracts.PdfRepositoryContract
import com.meadow.core.ai.domain.model.AiChatEvent
import com.meadow.core.ai.domain.model.AiChatUiState
import com.meadow.core.ai.domain.model.AiContextPayload
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ai.domain.model.Bookmark
import com.meadow.core.ai.domain.model.PdfDocument
import com.meadow.core.ai.domain.model.PdfSearchResult
import com.meadow.core.ai.domain.model.PdfState
import com.meadow.core.ai.domain.usecase.GenerateAiResponseUseCase
import com.meadow.core.ai.domain.usecase.LinkBookmarkToCatalogUseCase
import com.meadow.core.ai.domain.usecase.SaveBookmarkUseCase
import com.meadow.core.ai.domain.usecase.SearchPdfUseCase
import com.meadow.core.ai.pdf.BookmarkRepository
import com.meadow.core.ai.pdf.reference.ReferenceIndexer
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch

@HiltViewModel
class AiChatViewModel @Inject constructor(
    private val generateResponse: GenerateAiResponseUseCase,
    private val chatRepo: AiChatRepository,
    private val contextRepo: AiContextRepository,
    private val pdfRepository: PdfRepositoryContract,
    private val meadowUseCases: MeadowUseCases,
    private val searchPdf: SearchPdfUseCase,
    private val saveBookmark: SaveBookmarkUseCase,
    private val linkBookmarkToCatalog: LinkBookmarkToCatalogUseCase,
    private val bookmarkRepository: BookmarkRepository,
    private val referenceIndexer: ReferenceIndexer,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val contextPayload =
        savedStateHandle.get<AiContextPayload>("ai_context")

    private val contextLabel =
        savedStateHandle.get<String>("ai_context_label")

    private var scopeKey: String =
        contextPayload?.scopeKey
            ?: when {
                !contextPayload?.projectId.isNullOrBlank() ->
                    "project:${contextPayload!!.projectId}"
                !contextPayload?.seriesId.isNullOrBlank() ->
                    "series:${contextPayload!!.seriesId}"
                else -> "global"
            }

    fun setContext(payload: AiContextPayload?, label: String?) {
        _uiState.update { it.copy(context = payload, contextLabel = label) }

        scopeKey =
            payload?.scopeKey
                ?: when {
                    !payload?.projectId.isNullOrBlank() -> "project:${payload!!.projectId}"
                    !payload?.seriesId.isNullOrBlank() -> "series:${payload!!.seriesId}"
                    else -> "global"
                }

        viewModelScope.launch {
            refreshPersonaState(_uiState.value.persona)
        }
    }

    private val _uiState = MutableStateFlow(
        AiChatUiState(
            context = contextPayload,
            contextLabel = contextLabel
        )
    )
    val uiState: StateFlow<AiChatUiState> = _uiState

    private val _messages = MutableStateFlow<List<AiChatMessage>>(emptyList())
    val messages: StateFlow<List<AiChatMessage>> = _messages

    init {
        viewModelScope.launch {
            refreshPersonaState(_uiState.value.persona)
        }
    }

    fun onEvent(event: AiChatEvent) {
        when (event) {

            is AiChatEvent.PersonaChanged -> {
                viewModelScope.launch { refreshPersonaState(event.persona) }
            }

            is AiChatEvent.InputChanged -> {
                _uiState.update { it.copy(input = event.text) }
            }

            AiChatEvent.SendPressed -> {
                sendMessage()
            }

            is AiChatEvent.NewChatClicked -> {
                _uiState.update { it.copy(showNewChatDialog = true) }
            }

            is AiChatEvent.NewChatConfirmed -> {
                createNewChat(event.name)
            }

            AiChatEvent.NewChatDialogDismissed -> {
                _uiState.update { it.copy(showNewChatDialog = false) }
            }

            is AiChatEvent.ChatSelected -> {
                selectChat(event.chatId)
            }

            AiChatEvent.RenameChatClicked -> {
                _uiState.update { it.copy(showRenameChatDialog = true) }
            }

            AiChatEvent.RenameChatDialogDismissed -> {
                _uiState.update { it.copy(showRenameChatDialog = false) }
            }

            is AiChatEvent.RenameChatConfirmed -> {
                renameChat(event.name)
            }

            is AiChatEvent.DeleteChatClicked -> {
                deleteChat(event.chatId)
            }

            // ─── PDF events ─────────────────────

            is AiChatEvent.PdfDocumentSelected -> {
                _uiState.update { it.copy(selectedPdf = event.document) }
            }

            is AiChatEvent.OpenPdfViewer -> {
                openPdf(event.document)
            }

            AiChatEvent.ClosePdfViewer -> {
                closePdfViewer()
            }

            is AiChatEvent.OpenPdfAtPage -> {
                openPdfAtPage(event.documentPath, event.page)
            }

            AiChatEvent.ClearResponse -> {
                // You didn’t provide DAO methods for clearing messages only.
                // Leave it no-op until you add dao.deleteMessagesForChat(chatId).
                // (Or handle “clear” as a chat delete if that’s what you want.)
            }

            is AiChatEvent.ToggleMeadowPersonaContexts -> {
                _uiState.update { it.copy(includeMeadowPersonaContexts = event.enabled) }
            }

            AiChatEvent.ReindexMeadowPdfs -> {
                reindexMeadow()
            }
        }
    }

    private suspend fun refreshPersonaState(persona: AiPersona) {
        val chats = chatRepo.listChats(
            scopeKey = scopeKey,
            personaKey = persona.name
        )

        // Persona change should reset selection/messages
        _messages.value = emptyList()

        _uiState.update {
            it.copy(
                persona = persona,
                chatSummaries = chats,
                selectedChatId = null,
                selectedChatName = null
            )
        }

        if (persona == AiPersona.Meadow) {
            loadPdfDocuments()
        }
    }

    private fun loadPdfDocuments() {
        viewModelScope.launch {
            val docs = pdfRepository.listDocuments()

            _uiState.update {
                it.copy(
                    pdfDocuments = docs,
                    selectedPdf = it.selectedPdf ?: docs.firstOrNull()
                )
            }

            // optional indexing hook
            referenceIndexer.ensureIndexed(docs)
        }
    }

    private fun reindexMeadow() {
        viewModelScope.launch {
            val docs = pdfRepository.listDocuments()
            referenceIndexer.ensureIndexed(docs)
        }
    }

    private fun createNewChat(name: String) {
        val persona = _uiState.value.persona
        val finalName = name.trim().ifBlank { "New ${persona.name} Chat" }

        viewModelScope.launch {
            val summary = chatRepo.createChat(scopeKey, persona.name, finalName)
            val chats = chatRepo.listChats(scopeKey, persona.name)

            _uiState.update {
                it.copy(
                    chatSummaries = chats,
                    selectedChatId = summary.id,
                    selectedChatName = summary.name,
                    showNewChatDialog = false
                )
            }

            _messages.value = emptyList()
        }
    }

    private fun selectChat(chatId: String) {
        viewModelScope.launch {
            val msgs = chatRepo.listMessages(chatId)

            val persona = _uiState.value.persona
            val chats = chatRepo.listChats(scopeKey, persona.name)

            _uiState.update {
                it.copy(
                    selectedChatId = chatId,
                    selectedChatName = chats.firstOrNull { c -> c.id == chatId }?.name
                )
            }

            _messages.value = msgs
        }
    }

    private fun renameChat(newName: String) {
        val chatId = _uiState.value.selectedChatId ?: return
        val trimmed = newName.trim()
        if (trimmed.isBlank()) return

        viewModelScope.launch {
            chatRepo.renameChat(chatId, trimmed)

            val persona = _uiState.value.persona
            val chats = chatRepo.listChats(scopeKey, persona.name)

            _uiState.update {
                it.copy(
                    chatSummaries = chats,
                    selectedChatName = chats.firstOrNull { c -> c.id == chatId }?.name,
                    showRenameChatDialog = false
                )
            }
        }
    }

    private fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepo.hardDeleteChat(chatId)

            val persona = _uiState.value.persona
            val chats = chatRepo.listChats(scopeKey, persona.name)

            val selected = _uiState.value.selectedChatId
            val shouldClear = selected == chatId

            _uiState.update {
                it.copy(
                    chatSummaries = chats,
                    selectedChatId = if (shouldClear) null else selected,
                    selectedChatName = if (shouldClear) null else it.selectedChatName
                )
            }

            if (shouldClear) _messages.value = emptyList()
        }
    }

    private fun sendMessage() {
        val state = _uiState.value
        val chatId = state.selectedChatId ?: return

        val text = state.input.trim()
        if (text.isBlank()) return

        val persona = state.persona

        viewModelScope.launch {
            chatRepo.appendUser(chatId, text)
            _messages.value = chatRepo.listMessages(chatId)
            _uiState.update { it.copy(isLoading = true, input = "") }

            val contextPrompt = buildContextPrompt()

            val replyContent: String =
                if (persona == AiPersona.Meadow) {
                    meadowUseCases.answerWithLibrary(
                        question = contextPrompt + text,
                        scopeKey = scopeKey,
                        includePersonaContexts = state.includeMeadowPersonaContexts
                    ).content
                } else {
                    generateResponse(
                        persona = persona,
                        input = contextPrompt + text
                    ).content
                }

            chatRepo.appendAssistant(chatId, replyContent)
            _messages.value = chatRepo.listMessages(chatId)

            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private suspend fun buildContextPrompt(): String {
        val persona = _uiState.value.persona
        val contexts = contextRepo.resolveInjectedContexts(
            persona = persona,
            scopeKey = scopeKey,
            includeScoped = true
        )
        val block = contextRepo.buildContextBlock(contexts)
        return if (block.isBlank()) "" else "$block\n\n"
    }

    // ─────────────────────────────────────────────────────────────
    // PDF VIEWER + SEARCH + BOOKMARKS (matches your PdfState + repos)
    // ─────────────────────────────────────────────────────────────

    private fun openPdf(document: PdfDocument) {
        viewModelScope.launch {
            val bookmarks = bookmarkRepository
                .getBookmarksForDocument(document.path)
                .first()

            _uiState.update { s ->
                s.copy(
                    openViewer = true,
                    selectedPdf = document,
                    pdfState = PdfState(
                        documentPath = document.path,
                        query = "",
                        searchResults = emptyList(),
                        bookmarks = bookmarks,
                        showLinkDialog = false,
                        catalogId = "",
                        currentDocument = document,
                        currentPage = 0,
                        projectId = contextPayload?.projectId
                    )
                )
            }
        }
    }

    private fun closePdfViewer() {
        _uiState.update { s ->
            s.copy(
                openViewer = false,
                pdfState = null
            )
        }
    }

    private fun openPdfAtPage(documentPath: String, page: Int) {
        val doc =
            uiState.value.pdfDocuments.firstOrNull { it.path == documentPath }
                ?: uiState.value.selectedPdf
                ?: return

        viewModelScope.launch {
            val bookmarks = bookmarkRepository
                .getBookmarksForDocument(doc.path)
                .first()

            _uiState.update { s ->
                s.copy(
                    openViewer = true,
                    selectedPdf = doc,
                    pdfState = PdfState(
                        documentPath = doc.path,
                        query = s.pdfState?.query ?: "",
                        searchResults = s.pdfState?.searchResults ?: emptyList(),
                        bookmarks = bookmarks,
                        showLinkDialog = false,
                        catalogId = "",
                        currentDocument = doc,
                        currentPage = page,
                        projectId = contextPayload?.projectId
                    )
                )
            }
        }
    }

    fun searchInPdf(query: String) {
        val doc = uiState.value.selectedPdf ?: return

        viewModelScope.launch {
            val results: List<PdfSearchResult> = searchPdf(doc, query)

            _uiState.update { s ->
                val current = s.pdfState
                if (current == null) s
                else s.copy(
                    pdfState = current.copy(
                        query = query,
                        searchResults = results
                    )
                )
            }
        }
    }

    fun addBookmark(page: Int) {
        val doc = uiState.value.selectedPdf ?: return

        viewModelScope.launch {
            val bookmark = Bookmark(
                documentPath = doc.path,
                pageNumber = page,
                note = null,
                linkedCatalogId = null,
                projectId = contextPayload?.projectId
            )

            saveBookmark(bookmark)

            val updated = bookmarkRepository
                .getBookmarksForDocument(doc.path)
                .first()

            _uiState.update { s ->
                val current = s.pdfState
                if (current == null) s
                else s.copy(
                    pdfState = current.copy(
                        bookmarks = updated,
                        currentPage = page
                    )
                )
            }
        }
    }

    fun openBookmark(bookmark: Bookmark) {
        _uiState.update { s ->
            val current = s.pdfState
            if (current == null) s
            else s.copy(
                pdfState = current.copy(currentPage = bookmark.pageNumber)
            )
        }
    }

    fun linkBookmarkToCatalog(catalogId: String) {
        val doc = uiState.value.selectedPdf ?: return
        val pdfState = uiState.value.pdfState ?: return

        // Link the bookmark on the current page (simple + deterministic)
        val target = pdfState.bookmarks.firstOrNull {
            it.documentPath == doc.path && it.pageNumber == pdfState.currentPage
        } ?: return

        viewModelScope.launch {
            linkBookmarkToCatalog(target, catalogId)

            val updated = bookmarkRepository
                .getBookmarksForDocument(doc.path)
                .first()

            _uiState.update { s ->
                val current = s.pdfState
                if (current == null) s
                else s.copy(
                    pdfState = current.copy(
                        bookmarks = updated,
                        showLinkDialog = false,
                        catalogId = ""
                    )
                )
            }
        }
    }
}