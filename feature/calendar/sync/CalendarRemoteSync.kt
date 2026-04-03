package com.meadow.feature.calendar.sync

import com.meadow.feature.calendar.data.local.CalendarDao
import com.meadow.feature.calendar.data.local.CalendarEventEntity
import com.meadow.feature.calendar.data.mappers.CalendarEventMapper
import com.meadow.feature.calendar.data.preferences.CalendarPreferences
import com.meadow.feature.calendar.sync.google.CalendarRemoteDataSource
import com.meadow.feature.calendar.sync.google.GoogleCalendarMapper
import java.time.Instant
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Singleton
class CalendarRemoteSync @Inject constructor(
    private val remote: CalendarRemoteDataSource,
    private val dao: CalendarDao,
    private val prefs: CalendarPreferences
) {

    companion object {
        private const val MEADOW_CALENDAR_SUMMARY = "Meadow"
        private const val SYNC_WINDOW_PAST_DAYS = 365L
        private const val SYNC_WINDOW_FUTURE_DAYS = 365L
    }

    suspend fun sync(): Boolean = withContext(Dispatchers.IO) {
        val calendarId = ensureMeadowCalendarId()
        val pushOk = pushDirty(calendarId)
        val pullOk = pullRemote(calendarId)
        pushOk && pullOk
    }

    private suspend fun ensureMeadowCalendarId(): String {
        prefs.getMeadowCalendarId()?.let { return it }

        val listResp = remote.listCalendars()
        if (listResp.isSuccessful) {
            val existing = listResp.body()?.items
                ?.firstOrNull { it.summary == MEADOW_CALENDAR_SUMMARY }
            val id = existing?.id
            if (!id.isNullOrBlank()) {
                prefs.setMeadowCalendarId(id)
                return id
            }
        }

        val createResp = remote.createCalendar(MEADOW_CALENDAR_SUMMARY)
        if (createResp.isSuccessful) {
            val id = createResp.body()?.id ?: "primary"
            prefs.setMeadowCalendarId(id)
            return id
        }

        prefs.setMeadowCalendarId("primary")
        return "primary"
    }

    private suspend fun pushDirty(calendarId: String): Boolean {
        val dirtyIds = dao.getDirtyIds()
        if (dirtyIds.isEmpty()) return true

        val dirtyItems = dao.getByIds(dirtyIds)
        var allOk = true

        dirtyItems.forEach { entity ->
            try {
                if (entity.isDeleted) {
                    val googleId = entity.googleEventId
                    if (!googleId.isNullOrBlank()) {
                        val resp = remote.deleteEvent(calendarId = calendarId, eventId = googleId)
                        if (resp.isSuccessful) {
                            dao.deleteByLocalId(entity.localId)
                        } else {
                            allOk = false
                            dao.updateFlags(
                                localId = entity.localId,
                                dirty = true,
                                hasConflict = false,
                                lastError = resp.errorBody()?.string()
                            )
                        }
                    } else {
                        dao.deleteByLocalId(entity.localId)
                    }
                    return@forEach
                }

                val domain = CalendarEventMapper.toDomain(entity).copy(
                    googleCalendarId = calendarId,
                    isDirty = true
                )

                val gEvent = GoogleCalendarMapper.toGoogle(domain)

                if (entity.googleEventId.isNullOrBlank()) {
                    val resp = remote.insertEvent(calendarId = calendarId, event = gEvent)
                    if (resp.isSuccessful) {
                        val created = resp.body()
                        val updated = entity.copy(
                            googleCalendarId = calendarId,
                            googleEventId = created?.id,
                            etag = created?.etag,
                            remoteUpdatedAt = created?.updated?.let { OffsetDateTime.parse(it).toInstant().toEpochMilli() },
                            isDirty = false,
                            lastSyncError = null
                        )
                        dao.upsert(updated)
                    } else {
                        allOk = false
                        dao.updateFlags(
                            localId = entity.localId,
                            dirty = true,
                            hasConflict = false,
                            lastError = resp.errorBody()?.string()
                        )
                    }
                } else {
                    val resp = remote.patchEvent(
                        calendarId = calendarId,
                        eventId = entity.googleEventId!!,
                        event = gEvent
                    )
                    if (resp.isSuccessful) {
                        val updatedRemote = resp.body()
                        val updated = entity.copy(
                            googleCalendarId = calendarId,
                            etag = updatedRemote?.etag,
                            remoteUpdatedAt = updatedRemote?.updated?.let { OffsetDateTime.parse(it).toInstant().toEpochMilli() },
                            isDirty = false,
                            lastSyncError = null
                        )
                        dao.upsert(updated)
                    } else {
                        allOk = false
                        dao.updateFlags(
                            localId = entity.localId,
                            dirty = true,
                            hasConflict = false,
                            lastError = resp.errorBody()?.string()
                        )
                    }
                }
            } catch (e: Exception) {
                allOk = false
                dao.updateFlags(
                    localId = entity.localId,
                    dirty = true,
                    hasConflict = false,
                    lastError = e.message ?: e::class.java.simpleName
                )
            }
        }

        return allOk
    }

    private suspend fun pullRemote(calendarId: String): Boolean {
        val now = Instant.now()
        val timeMin = now.minusSeconds(SYNC_WINDOW_PAST_DAYS * 24 * 3600).toString()
        val timeMax = now.plusSeconds(SYNC_WINDOW_FUTURE_DAYS * 24 * 3600).toString()

        val syncToken = prefs.getSyncToken()

        val upserts = mutableListOf<CalendarEventEntity>()
        var pageToken: String? = null
        var nextSyncToken: String? = null

        while (true) {
            val resp = remote.listEvents(
                calendarId = calendarId,
                timeMin = if (syncToken == null) timeMin else null,
                timeMax = if (syncToken == null) timeMax else null,
                syncToken = syncToken,
                showDeleted = true,
                pageToken = pageToken
            )

            if (!resp.isSuccessful) {
                if (resp.code() == 410) {
                    prefs.setSyncToken(null)
                }
                return false
            }

            val body = resp.body() ?: return false
            val items = body.items.orEmpty()
            nextSyncToken = body.nextSyncToken ?: nextSyncToken

            for (g in items) {
                val d = GoogleCalendarMapper.fromGoogle(calendarId, g) ?: continue
                val existing = d.googleEventId?.let { dao.getByGoogleId(it) }

                if (d.isDeleted) {
                    if (existing != null) {
                        dao.deleteByLocalId(existing.localId)
                    }
                    continue
                }

                val merged = if (existing == null) {
                    d
                } else {
                    val remoteTs = d.remoteUpdatedAt ?: 0L
                    val localTs = existing.localUpdatedAt

                    when {
                        existing.isDirty && remoteTs > localTs -> d.copy(localId = existing.localId, hasConflict = true)
                        existing.isDirty -> null
                        else -> d.copy(localId = existing.localId)
                    }
                }

                if (merged != null) {
                    upserts.add(
                        CalendarEventMapper.toEntity(
                            merged.copy(
                                googleCalendarId = calendarId,
                                isDirty = false,
                                lastSyncError = null
                            )
                        )
                    )
                }
            }

            pageToken = body.nextPageToken
            if (pageToken.isNullOrBlank()) break
        }

        if (upserts.isNotEmpty()) {
            dao.upsertAll(upserts)
        }

        nextSyncToken?.let { prefs.setSyncToken(it) }
        return true
    }
}