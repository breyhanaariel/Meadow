package com.meadow.app.domain.model

/**
 * Syncable.kt
 *
 * Interface to mark objects that can be synced
 * with Firebase or other remote services.
 */

interface Syncable {
    val id: String
    val projectId: String
}
