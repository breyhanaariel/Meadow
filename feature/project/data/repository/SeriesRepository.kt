package com.meadow.feature.project.data.repository

import com.meadow.feature.project.data.local.SeriesDao
import com.meadow.feature.project.data.local.SeriesEntity
import com.meadow.feature.project.domain.model.Series
import com.meadow.feature.project.domain.repository.SeriesRepositoryContract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SeriesRepository @Inject constructor(
    private val seriesDao: SeriesDao
) : SeriesRepositoryContract {


    override fun observeAllSeries(): Flow<List<Series>> =
        seriesDao.observeAll()
            .map { entities -> entities.map { it.toDomain() } }

    override fun observeSeriesById(id: String): Flow<Series?> =
        seriesDao.observeById(id)
            .map { it?.toDomain() }

    override suspend fun getSeriesById(id: String): Series? =
        seriesDao.getById(id)?.toDomain()

    override suspend fun getAllSeriesOnce(): List<Series> =
        seriesDao.getAll().map { it.toDomain() }

    override suspend fun createSeries(title: String): String {
        val id = UUID.randomUUID().toString()
        seriesDao.insert(
            SeriesEntity(
                id = id,
                title = title,
                projectIds = "",
                sharedFieldIds = encodeFieldIds(emptyList()),
                sharedCatalogFieldIds = encodeSet(emptySet())
            )
        )
        return id
    }

    override suspend fun renameSeries(seriesId: String, newTitle: String) {
        seriesDao.updateTitle(seriesId, newTitle)
    }

    override suspend fun updateSeries(series: Series) {
        seriesDao.updateSeries(
            seriesId = series.id,
            title = series.title,
            sharedFieldIds = encodeFieldIds(series.sharedFieldIds),
            sharedCatalogFieldIds = encodeSet(series.sharedCatalogFieldIds)
        )
    }

    override suspend fun deleteSeries(seriesId: String) {
        seriesDao.deleteSeries(seriesId)
    }

    override suspend fun setSeriesFields(seriesId: String, fieldIds: List<String>) {
        seriesDao.updateSharedFields(
            seriesId = seriesId,
            sharedFieldIds = encodeFieldIds(fieldIds)
        )
    }

    override suspend fun addSeriesField(seriesId: String, fieldId: String) {
        val current =
            seriesDao.getById(seriesId)
                ?.toDomain()
                ?.sharedFieldIds
                ?: emptyList()

        if (fieldId in current) return
        setSeriesFields(seriesId, current + fieldId)
    }

    override suspend fun removeSeriesField(seriesId: String, fieldId: String) {
        val current =
            seriesDao.getById(seriesId)
                ?.toDomain()
                ?.sharedFieldIds
                ?: emptyList()

        if (fieldId !in current) return
        setSeriesFields(seriesId, current - fieldId)
    }


    private fun encodeFieldIds(fieldIds: List<String>): String =
        fieldIds.joinToString(",")

    private fun decodeFieldIds(raw: String): List<String> =
        raw.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }

    private fun SeriesEntity.toDomain(): Series =
        Series(
            id = id,
            title = title,
            projectIds = decodeList(projectIds),
            sharedFieldIds = decodeFieldIds(sharedFieldIds),
            sharedCatalogFieldIds = decodeSet(sharedCatalogFieldIds)
        )
    private fun encodeList(ids: List<String>): String = ids.joinToString(",")
    private fun decodeList(raw: String): List<String> =
        raw.split(",").map { it.trim() }.filter { it.isNotBlank() }

    private fun encodeSet(ids: Set<String>): String = ids.joinToString(",")
    private fun decodeSet(raw: String): Set<String> =
        raw.split(",").map { it.trim() }.filter { it.isNotBlank() }.toSet()

    override suspend fun setSeriesCatalogFields(seriesId: String, fieldIds: Set<String>) {
        seriesDao.updateSharedCatalogFields(seriesId, encodeSet(fieldIds))
    }

    override suspend fun addSeriesCatalogField(seriesId: String, fieldId: String) {
        val current = seriesDao.getById(seriesId)?.toDomain()?.sharedCatalogFieldIds ?: emptySet()
        if (fieldId in current) return
        setSeriesCatalogFields(seriesId, current + fieldId)
    }

    override suspend fun removeSeriesCatalogField(seriesId: String, fieldId: String) {
        val current = seriesDao.getById(seriesId)?.toDomain()?.sharedCatalogFieldIds ?: emptySet()
        if (fieldId !in current) return
        setSeriesCatalogFields(seriesId, current - fieldId)
    }
    override suspend fun addProjectToSeries(
        seriesId: String,
        projectId: String
    ) {
        val entity = seriesDao.getById(seriesId) ?: return
        val series = entity.toDomain()
        if (projectId in series.projectIds) return
        seriesDao.updateSeries(
            seriesId = series.id,
            title = series.title,
            projectIds = encodeList(series.projectIds + projectId),
            sharedFieldIds = encodeFieldIds(series.sharedFieldIds),
            sharedCatalogFieldIds = encodeSet(series.sharedCatalogFieldIds)
        )
    }

    override suspend fun removeProjectFromSeries(
        seriesId: String,
        projectId: String
    ) {
        val entity = seriesDao.getById(seriesId) ?: return
        val series = entity.toDomain()
        seriesDao.updateSeries(
            seriesId = series.id,
            title = series.title,
            projectIds = encodeList(series.projectIds - projectId),
            sharedFieldIds = encodeFieldIds(series.sharedFieldIds),
            sharedCatalogFieldIds = encodeSet(series.sharedCatalogFieldIds)
        )
    }
}
