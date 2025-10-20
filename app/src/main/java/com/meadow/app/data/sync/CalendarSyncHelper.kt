package com.meadow.app.data.sync

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.calendar.Calendar
import com.google.api.services.calendar.model.Event
import com.google.api.services.calendar.model.EventDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class CalendarSyncHelper(private val ctx: Context) {
    private var calendarService: Calendar? = null

    suspend fun init() = withContext(Dispatchers.IO) {
        val account = GoogleSignIn.getLastSignedInAccount(ctx)
        if (account == null) {
            Log.e("CalendarSync", "Not signed in")
            return@withContext
        }

        val credential = GoogleAccountCredential.usingOAuth2(
            ctx, listOf("https://www.googleapis.com/auth/calendar")
        )
        credential.selectedAccount = account.account
        calendarService = Calendar.Builder(
            AndroidHttp.newCompatibleTransport(),
            GsonFactory(),
            credential
        ).setApplicationName("Meadow").build()
    }

    suspend fun addDeadlineEvent(title: String, description: String, startMillis: Long, endMillis: Long) {
        withContext(Dispatchers.IO) {
            val event = Event()
                .setSummary(title)
                .setDescription(description)
                .setStart(EventDateTime().setDateTime(com.google.api.client.util.DateTime(startMillis)))
                .setEnd(EventDateTime().setDateTime(com.google.api.client.util.DateTime(endMillis)))
            calendarService?.events()?.insert("primary", event)?.execute()
            Log.d("CalendarSync", "Event added: $title")
        }
    }
}
