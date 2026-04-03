package com.meadow.feature.wiki.domain.repository

import com.meadow.core.common.functional.ResultX
import com.meadow.feature.wiki.domain.model.WikiEntry
import kotlinx.coroutines.flow.Flow

interface WikiRepositoryContract {
    fun getEntries(projectId: String): Flow<ResultX<List<WikiEntry>>>
    suspend fun addEntry(entry: WikiEntry): ResultX<Unit>
}
