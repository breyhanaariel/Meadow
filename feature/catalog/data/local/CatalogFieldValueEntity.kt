package com.meadow.feature.catalog.data.local

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "catalog_field_values",
    primaryKeys = ["catalogItemId", "fieldId"],
    indices = [Index("catalogItemId")]
)
data class CatalogFieldValueEntity(
    val catalogItemId: String,
    val fieldId: String,
    val rawValue: String?
)
