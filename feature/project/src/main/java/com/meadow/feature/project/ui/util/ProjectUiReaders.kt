package com.meadow.feature.project.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.project.domain.model.Project


/* ─── TITLE ────────────────────────────────── */

@Composable
fun Project.readTitle(): String =
    readTitleOrNull()
        ?: stringResource(CoreUiR.string.untitled)

/* ─── COVER IMAGE ──────────────────────────── */
@Composable
fun Project.readCoverImage(): String? =
    resolveCoverImageOrNull()
