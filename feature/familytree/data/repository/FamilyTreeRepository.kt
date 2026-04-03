package com.meadow.feature.familytree.data.repository

import com.meadow.feature.familytree.domain.model.FamilyLink
import com.meadow.feature.familytree.domain.repository.FamilyTreeRepositoryContract
import com.meadow.core.common.functional.ResultX
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import com.meadow.core.common.functional.ResultX.Companion.success


@Singleton
class FamilyTreeRepository @Inject constructor() : FamilyTreeRepositoryContract {

    private val map = mutableMapOf<String, MutableStateFlow<List<FamilyLink>>>()

    override fun getFamily(projectId: String): Flow<ResultX<List<FamilyLink>>> =
        map.getOrPut(projectId) { MutableStateFlow(emptyList()) }
            .map { ResultX.success(it) }

    override suspend fun addFamilyLink(link: FamilyLink): ResultX<Unit> {
        val flow = map.getOrPut(link.projectId) { MutableStateFlow(emptyList()) }
        flow.value = flow.value + link
        return ResultX.success(Unit)
    }
}
