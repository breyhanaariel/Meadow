package com.meadow.core.node.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NodeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: NodeEntity)

    @Query("SELECT * FROM nodes WHERE nodeId = :nodeId LIMIT 1")
    suspend fun get(nodeId: String): NodeEntity?

    @Query("SELECT * FROM nodes WHERE nodeId = :nodeId LIMIT 1")
    fun observe(nodeId: String): Flow<NodeEntity?>

    @Query(
        "SELECT * FROM nodes WHERE projectId = :projectId AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    suspend fun listByProject(projectId: String, includeDeleted: Boolean): List<NodeEntity>

    @Query(
        "SELECT * FROM nodes WHERE projectId = :projectId AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    fun observeByProject(projectId: String, includeDeleted: Boolean): Flow<List<NodeEntity>>

    @Query(
        "SELECT * FROM nodes WHERE seriesId = :seriesId AND projectId IS NULL AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    suspend fun listBySeries(seriesId: String, includeDeleted: Boolean): List<NodeEntity>

    @Query(
        "SELECT * FROM nodes WHERE seriesId = :seriesId AND projectId IS NULL AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    fun observeBySeries(seriesId: String, includeDeleted: Boolean): Flow<List<NodeEntity>>

    @Query(
        "SELECT * FROM nodes WHERE projectId = :projectId AND seriesId = :seriesId AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    suspend fun listByProjectInSeries(projectId: String, seriesId: String, includeDeleted: Boolean): List<NodeEntity>

    @Query(
        "SELECT * FROM nodes WHERE projectId = :projectId AND seriesId = :seriesId AND (:includeDeleted = 1 OR isDeleted = 0) ORDER BY orderKey ASC"
    )
    fun observeByProjectInSeries(projectId: String, seriesId: String, includeDeleted: Boolean): Flow<List<NodeEntity>>

    @Query("UPDATE nodes SET parentNodeId = :parentNodeId, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE nodeId = :nodeId")
    suspend fun updateParent(nodeId: String, parentNodeId: String?, updatedAt: Long)

    @Query("UPDATE nodes SET orderKey = :orderKey, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE nodeId = :nodeId")
    suspend fun updateOrder(nodeId: String, orderKey: Long, updatedAt: Long)

    @Query("UPDATE nodes SET title = :title, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE nodeId = :nodeId")
    suspend fun updateTitle(nodeId: String, title: String, updatedAt: Long)

    @Query("UPDATE nodes SET content = :content, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE nodeId = :nodeId")
    suspend fun updateContent(nodeId: String, content: String?, updatedAt: Long)

    @Query("UPDATE nodes SET isDeleted = 1, updatedAt = :updatedAt, isDirty = 1, localVersion = localVersion + 1 WHERE nodeId = :nodeId")
    suspend fun softDelete(nodeId: String, updatedAt: Long)
}
