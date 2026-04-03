package com.meadow.core.ai.data.chat

import androidx.room.TypeConverter

class SyncStateConverter {

    @TypeConverter
    fun fromSyncState(state: SyncState): String =
        state.name

    @TypeConverter
    fun toSyncState(value: String): SyncState =
        SyncState.valueOf(value)
}
