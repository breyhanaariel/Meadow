package com.meadow.core.domain.model

interface Syncable {
    val id: String
    val updatedAt: Long
    val synced: Boolean
}