package com.meadow.feature.project.domain.repository

import com.meadow.feature.project.domain.model.Series
import kotlinx.coroutines.flow.Flow

interface SeriesRepositoryContract {
    fun observeAllSeries(): Flow<List<Series>>
    fun observeSeriesById(id: String): Flow<Series?>
    suspend fun getSeriesById(id: String): Series?
    suspend fun getAllSeriesOnce(): List<Series>
    suspend fun createSeries(title: String): String
    suspend fun renameSeries(seriesId: String, newTitle: String)
    suspend fun updateSeries(series: Series)
    suspend fun deleteSeries(seriesId: String)
    suspend fun setSeriesFields(seriesId: String, fieldIds: List<String>)
    suspend fun addSeriesField(seriesId: String, fieldId: String)
    suspend fun removeSeriesField(seriesId: String, fieldId: String)
    suspend fun setSeriesCatalogFields(seriesId: String, fieldIds: Set<String>)
    suspend fun addSeriesCatalogField(seriesId: String, fieldId: String)
    suspend fun removeSeriesCatalogField(seriesId: String, fieldId: String)
    suspend fun addProjectToSeries(seriesId: String, projectId: String)
    suspend fun removeProjectFromSeries(seriesId: String, projectId: String)
}
