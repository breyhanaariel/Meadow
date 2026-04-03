package com.meadow.feature.project.data.repository

import com.meadow.core.data.fields.FieldWithValue
import com.meadow.core.data.fields.repository.FieldHistoryRepository
import com.meadow.core.domain.model.HistoryOwnerType
import com.meadow.core.domain.model.HistorySource
import com.meadow.feature.project.data.local.ProjectDao
import com.meadow.feature.project.data.local.ProjectFieldValueDao
import com.meadow.feature.project.data.local.ProjectFieldValueEntity
import com.meadow.feature.project.data.mappers.ProjectEntityMapper
import com.meadow.feature.project.domain.model.Project
import com.meadow.feature.project.domain.model.ProjectSyncMeta
import com.meadow.feature.project.domain.registry.ProjectTemplateRegistry
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract
import com.meadow.feature.project.sync.work.ProjectSyncScheduler
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProjectRepository @Inject constructor(
    private val projectDao: ProjectDao,
    private val projectFieldValueDao: ProjectFieldValueDao,
    private val templateRegistry: ProjectTemplateRegistry,
    private val scheduler: ProjectSyncScheduler,
    private val fieldHistoryRepository: FieldHistoryRepository
) : ProjectRepositoryContract {

    private val now: Long get() = System.currentTimeMillis()

    override fun observeProjects(): Flow<List<Project>> =
        combine(
            projectDao.observeAll(),
            projectFieldValueDao.observeAll()
        ) { projectEntities, allValues ->
            projectEntities.map { entity ->
                val project = ProjectEntityMapper.toDomain(entity)

                val hydratedFields = hydrateFields(
                    projectId = project.id,
                    typeKey = project.type.key,
                    values = allValues.filter { it.projectId == project.id }
                )

                project.copy(fields = hydratedFields)
            }
        }

    override fun observeAllProjects(): Flow<List<Project>> = observeProjects()

    override fun observeProject(id: String): Flow<Project?> =
        combine(
            projectDao.observeById(id),
            projectFieldValueDao.observeValuesForProject(id)
        ) { entity, values ->
            entity ?: return@combine null

            val project = ProjectEntityMapper.toDomain(entity)

            val hydratedFields = hydrateFields(
                projectId = project.id,
                typeKey = project.type.key,
                values = values
            )

            project.copy(fields = hydratedFields)
        }

    override suspend fun getProjectById(id: String): Project? {
        val entity = projectDao.getById(id) ?: return null
        val project = ProjectEntityMapper.toDomain(entity)

        val values = projectFieldValueDao.valuesForProject(id)

        val hydratedFields = hydrateFields(
            projectId = project.id,
            typeKey = project.type.key,
            values = values
        )

        return project.copy(fields = hydratedFields)
    }

    override suspend fun getAllProjectsOnce(): List<Project> =
        observeProjects().first()

    override suspend fun createProject(project: Project) {
        val stamped = project.copy(
            updatedAt = project.updatedAt ?: now,
            syncMeta = project.syncMeta.copy(
                localVersion = project.syncMeta.localVersion.coerceAtLeast(1L),
                localUpdatedAt = project.updatedAt ?: now,
                isDirty = true,
                lastSyncError = null,
                retryCount = 0
            )
        )

        projectDao.upsert(ProjectEntityMapper.toEntity(stamped))

        stamped.fields.forEach { fwv ->
            projectFieldValueDao.upsert(
                ProjectFieldValueEntity(
                    projectId = stamped.id,
                    fieldId = fwv.definition.id,
                    rawValue = fwv.value?.rawValue?.toString(),
                    updatedAt = now
                )
            )
        }

        fieldHistoryRepository.recordMapDiff(
            ownerId = stamped.id,
            ownerType = HistoryOwnerType.PROJECT,
            oldValues = emptyMap(),
            newValues = stamped.fields.toHistoryMap(),
            source = HistorySource.MANUAL
        )
    }

    override suspend fun updateProject(project: Project) {
        val oldProject = getProjectById(project.id) ?: return

        val updatedAt = now
        val meta = project.syncMeta

        val updated = project.copy(
            updatedAt = updatedAt,
            syncMeta = meta.copy(
                localVersion = meta.localVersion + 1,
                localUpdatedAt = updatedAt,
                isDirty = true,
                lastSyncError = null
            )
        )

        projectDao.upsert(ProjectEntityMapper.toEntity(updated))

        updated.fields.forEach { fwv ->
            projectFieldValueDao.upsert(
                ProjectFieldValueEntity(
                    projectId = updated.id,
                    fieldId = fwv.definition.id,
                    rawValue = fwv.value?.rawValue?.toString(),
                    updatedAt = updatedAt
                )
            )
        }

        fieldHistoryRepository.recordMapDiff(
            ownerId = updated.id,
            ownerType = HistoryOwnerType.PROJECT,
            oldValues = oldProject.fields.toHistoryMap(),
            newValues = updated.fields.toHistoryMap(),
            source = HistorySource.MANUAL,
            timestamp = updatedAt
        )
    }

    override suspend fun deleteProject(id: String) {
        projectDao.getById(id)?.let { projectDao.delete(it) }
    }

    override suspend fun replaceAllProjects(projects: List<Project>) {
        /* KEEP: no history here by default, because bulk sync/import would spam history */
        projects.forEach { projectDao.upsert(ProjectEntityMapper.toEntity(it)) }
    }

    override suspend fun updateProjectSyncMeta(
        projectId: String,
        meta: ProjectSyncMeta
    ) {
        val project = getProjectById(projectId) ?: return
        projectDao.upsert(ProjectEntityMapper.toEntity(project.copy(syncMeta = meta)))
    }

    override suspend fun syncProject(projectId: String) {
        scheduler.triggerManual()
    }

    override suspend fun syncAllProjects() {
        scheduler.triggerManual()
    }

    suspend fun getDirtyIds(): List<String> =
        projectDao.getDirtyProjectIds()

    suspend fun markSyncFailure(projectIds: List<String>, error: String) {
        projectIds.forEach { id ->
            val p = projectDao.getById(id) ?: return@forEach
            val retry = (p.retryCount + 1).coerceAtMost(10)
            projectDao.updateDirtyAndError(
                projectId = id,
                isDirty = 1,
                error = error,
                retryCount = retry
            )
        }
    }

    override suspend fun updateProjectField(
        projectId: String,
        fieldKey: String,
        value: String?
    ) {
        val oldProject = getProjectById(projectId) ?: return

        val updatedFields = oldProject.fields.map { field ->
            if (field.definition.key == fieldKey || field.definition.id == fieldKey) {
                val updatedValue = when {
                    value == null -> null
                    field.value != null -> field.value.copy(rawValue = value)
                    else -> null
                }
                field.copy(value = updatedValue)
            } else {
                field
            }
        }

        val updatedAt = now
        val meta = oldProject.syncMeta

        val updatedProject = oldProject.copy(
            fields = updatedFields,
            updatedAt = updatedAt,
            syncMeta = meta.copy(
                localVersion = meta.localVersion + 1,
                localUpdatedAt = updatedAt,
                isDirty = true,
                lastSyncError = null
            )
        )

        projectDao.upsert(ProjectEntityMapper.toEntity(updatedProject))

        val match = updatedFields.firstOrNull {
            it.definition.key == fieldKey || it.definition.id == fieldKey
        } ?: return

        projectFieldValueDao.upsert(
            ProjectFieldValueEntity(
                projectId = projectId,
                fieldId = match.definition.id,
                rawValue = match.value?.rawValue?.toString(),
                updatedAt = updatedAt
            )
        )

        fieldHistoryRepository.recordMapDiff(
            ownerId = projectId,
            ownerType = HistoryOwnerType.PROJECT,
            oldValues = oldProject.fields.toHistoryMap(),
            newValues = updatedProject.fields.toHistoryMap(),
            source = HistorySource.MANUAL,
            timestamp = updatedAt
        )
    }

    override suspend fun updateProjectSeries(
        projectId: String,
        seriesId: String?
    ) {
        val oldProject = getProjectById(projectId) ?: return

        projectDao.updateSeries(projectId, seriesId)

        fieldHistoryRepository.recordSingleChange(
            ownerId = projectId,
            ownerType = HistoryOwnerType.PROJECT,
            fieldId = "project.seriesId",
            oldValue = oldProject.seriesId,
            newValue = seriesId,
            source = HistorySource.MANUAL,
            timestamp = now
        )
    }

    private fun hydrateFields(
        projectId: String,
        typeKey: String,
        values: List<ProjectFieldValueEntity>
    ): List<FieldWithValue> {
        val defs = templateRegistry.getTemplate(typeKey)?.fields.orEmpty()
        if (defs.isEmpty()) return emptyList()

        val valueByFieldId = values.associateBy { it.fieldId }

        return defs.map { def ->
            val raw = valueByFieldId[def.id]?.rawValue
            FieldWithValue(
                definition = def,
                value = raw?.let {
                    com.meadow.core.data.fields.FieldValue(
                        fieldId = def.id,
                        ownerItemId = projectId,
                        rawValue = it
                    )
                }
            )
        }
    }

    private fun List<FieldWithValue>.toHistoryMap(): Map<String, String?> =
        associate { field ->
            field.definition.id to field.value?.rawValue
                ?.toString()
                ?.trim()
                ?.ifBlank { null }
        }
}