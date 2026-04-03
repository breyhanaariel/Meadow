package com.meadow.feature.project.api

import kotlinx.coroutines.flow.Flow

interface SeriesSelector {
    fun observeAvailableSeries(
        projectId: String? = null
    ): Flow<List<SeriesSelectorItem>>
}

data class SeriesSelectorItem(
    val id: String,
    val title: String?,
    val projectId: String?
)
