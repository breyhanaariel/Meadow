package com.meadow.core.sync.engine

interface SyncConflictResolver<T> {
    fun resolve(local: T, remote: T): T
}
