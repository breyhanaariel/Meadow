package com.meadow.core.ai.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.meadow.core.ai.R as R
import com.meadow.core.ai.domain.model.AiChatEvent
import com.meadow.core.ai.domain.model.AiPersona
import com.meadow.core.ui.R as CoreUiR
import com.meadow.core.ui.components.tokens.ComponentRoles
import kotlinx.coroutines.delay

/* ─── PDF LINK MODEL ───────────────────── */
/* Represents a parsed PDF reference with target page */
data class PdfPageLink(
    val path: String,
    val page: Int
)

/* ─── PDF LINK PARSING ───────────────────── */
/* Matches inline PDF page references embedded in AI responses */
private val pdfPageRegex =
    Regex("""\[\[pdf_page\|(.+?)\|(\d+)]]""")

/* ─── PDF LINK EXTRACTION ───────────────────── */
/* Removes PDF markup from text and returns extracted page links */
private fun extractPdfLinks(text: String): Pair<String, List<PdfPageLink>> {
    val links = mutableListOf<PdfPageLink>()

    val cleaned = pdfPageRegex.replace(text) { match ->
        val path = match.groupValues[1]
        val page = match.groupValues[2].toInt()
        links += PdfPageLink(path, page)
        ""
    }

    return cleaned.trim() to links
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiResponseCard(
    persona: AiPersona,
    message: String,
    isUser: Boolean,
    onEvent: (AiChatEvent) -> Unit,
    modifier: Modifier = Modifier
) {

    /* ─── SYSTEM SERVICES ───────────────────── */
    /* Clipboard access for copy-to-clipboard action */
    val clipboard = LocalClipboardManager.current

    /* ─── UI STATE ───────────────────── */
    /* Tracks temporary "copied" feedback state */
    var copied by remember { mutableStateOf(false) }

    /* ─── MESSAGE STYLING ───────────────────── */
    /* Bubble color varies based on message author */
    val bubbleColor = if (isUser) {
        MaterialTheme.colorScheme.surfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surfaceContainer
    }

    /* ─── MESSAGE ROW ───────────────────── */
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {

        /* ─── PERSONA ICON ───────────────────── */
        /* Displayed only for AI messages */
        if (!isUser) {
            Icon(
                painter = painterResource(persona.iconRes),
                contentDescription = persona.displayName,
                modifier = Modifier
                    .size(44.dp)
                    .padding(end = 6.dp),
                tint = Color.Unspecified
            )
        }

        /* ─── MESSAGE BUBBLE ───────────────────── */
        Surface(
            color = bubbleColor,
            tonalElevation = ComponentRoles.Elevation.Low,
            shape = ComponentRoles.Shape.BubbleSoft,
            modifier = modifier
                .widthIn(max = 280.dp)
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {

                /* ─── MESSAGE CONTENT ───────────────────── */
                /* Strip user prefix and extract embedded PDF links */
                val (displayText, pdfLinks) = remember(message) {
                    extractPdfLinks(message.removePrefix("YOU:").trim())
                }

                Text(
                    text = displayText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (isUser)
                        Color.White
                    else
                        MaterialTheme.colorScheme.primary
                )

                /* ─── PDF PAGE CHIPS ───────────────────── */
                /* Render clickable PDF page references for Meadow persona */
                if (!isUser && pdfLinks.isNotEmpty()) {
                    Spacer(Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        pdfLinks.forEach { link ->
                            AssistChip(
                                onClick = {
                                    onEvent(
                                        AiChatEvent.OpenPdfAtPage(
                                            documentPath = link.path,
                                            page = link.page
                                        )
                                    )
                                },
                                label = {
                                    Text(
                                        text = stringResource(
                                            R.string.ai_open_pdf_page,
                                            link.page
                                        )
                                    )
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                /* ─── COPY ACTION ───────────────────── */
                Text(
                    text = stringResource(id = CoreUiR.string.action_copy),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            /* Uses Compose clipboard for cross-surface reliability */
                            clipboard.setText(AnnotatedString(displayText))
                            copied = true
                        }
                        .padding(4.dp)
                )

                /* ─── COPY CONFIRMATION ───────────────────── */
                if (copied) {
                    Text(
                        text = stringResource(id = CoreUiR.string.action_copied),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.primary
                    )

                    LaunchedEffect(Unit) {
                        delay(1200)
                        copied = false
                    }
                }
            }
        }
    }
}
