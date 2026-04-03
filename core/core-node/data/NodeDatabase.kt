package com.meadow.core.node.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        NodeEntity::class,
        NodeEdgeEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class NodeDatabase : RoomDatabase() {
    abstract fun nodeDao(): NodeDao
    abstract fun nodeEdgeDao(): NodeEdgeDao
}
