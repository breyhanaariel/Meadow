package com.meadow.feature.wiki.repository

import com.meadow.core.common.functional.ResultX
import com.meadow.feature.wiki.domain.model.WikiEntry
import com.meadow.feature.wiki.domain.repository.WikiRepositoryContract
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.core.common.functional.ResultX.Companion.success

@Singleton
class WikiRepository @Inject constructor() : WikiRepositoryContract {

    private val map = mutableMapOf<String, MutableStateFlow<List<WikiEntry>>>()

    override fun getEntries(projectId: String): Flow<ResultX<List<WikiEntry>>> =
        map.getOrPut(projectId) { MutableStateFlow(emptyList()) }
            .map { ResultX.success(it) }

    override suspend fun addEntry(entry: WikiEntry): ResultX<Unit> {
        val flow = map.getOrPut(entry.projectId) { MutableStateFlow(emptyList()) }
        flow.value = flow.value + entry
        return ResultX.success(Unit)
    }
}
