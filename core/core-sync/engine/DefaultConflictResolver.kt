package com.meadow.core.sync.engine

class DefaultConflictResolver<T> : SyncConflictResolver<T> {
    override fun resolve(local: T, remote: T): T {
        return remote
    }
}
