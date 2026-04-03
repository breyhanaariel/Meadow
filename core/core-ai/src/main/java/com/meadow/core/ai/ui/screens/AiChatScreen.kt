package com.meadow.core.ai.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.meadow.core.ai.domain.model.AiChatEvent
import com.meadow.core.ai.ui.components.AiChatPanel
import com.meadow.core.ai.ui.components.PdfViewer
import com.meadow.core.ai.viewmodel.AiChatViewModel

@Composable
fun AiChatScreen(
    navController: NavController,
    viewModel: AiChatViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    if (state.openViewer && state.pdfState != null) {
        PdfViewer(
            state = state.pdfState!!,
            onSearch = { query -> viewModel.searchInPdf(query) },
            onAddBookmark = { page -> viewModel.addBookmark(page) },
            onOpenBookmark = { bookmark -> viewModel.openBookmark(bookmark) },
            onLinkCatalog = { catalogId -> viewModel.linkBookmarkToCatalog(catalogId) },
            onDismissDialog = { viewModel.onEvent(AiChatEvent.ClosePdfViewer) },
            onBack = { viewModel.onEvent(AiChatEvent.ClosePdfViewer) }
        )
    } else {
        AiChatPanel(
            navController = navController,
            viewModel = viewModel,
            onDismiss = { navController.popBackStack() },
            onClose = { navController.popBackStack() }
        )
    }
}