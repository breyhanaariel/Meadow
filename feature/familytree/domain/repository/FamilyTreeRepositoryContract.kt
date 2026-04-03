package com.meadow.feature.familytree.domain.repository

import com.meadow.feature.familytree.domain.model.FamilyLink
import com.meadow.core.common.functional.ResultX
import kotlinx.coroutines.flow.Flow

interface FamilyTreeRepositoryContract {
    fun getFamily(projectId: String): Flow<ResultX<List<FamilyLink>>>
    suspend fun addFamilyLink(link: FamilyLink): ResultX<Unit>
}
