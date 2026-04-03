package com.meadow.feature.catalog.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.meadow.core.ui.R as CoreUiR
import com.meadow.feature.catalog.domain.model.CatalogItem

@Composable
fun CatalogItem.readTitle(): String =
    readTitleOrNull()
        ?: stringResource(CoreUiR.string.untitled)
