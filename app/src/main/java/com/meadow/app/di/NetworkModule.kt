package com.meadow.app.di

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/**
 * NetworkModule.kt
 *
 * Provides networking dependencies for Meadow’s cloud and API integrations.
 * This includes:
 * - Firebase Firestore and Storage for syncing project data and media.
 * - Google Drive/Calendar API access (using Retrofit + OkHttp).
 * - Coroutine dispatchers for safe background execution.
 *
 * Designed to be modular, future-proof, and compatible with Hilt dependency injection.
 */

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    // Base URLs for different services.
    private const val GOOGLE_DRIVE_BASE_URL = "https://www.googleapis.com/drive/v3/"
    private const val GOOGLE_CALENDAR_BASE_URL = "https://www.googleapis.com/calendar/v3/"

    /**
     * Provides a default CoroutineDispatcher for IO operations.
     * This ensures background network and database work are performed safely off the main thread.
     */
    @Provides
    @Singleton
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * Provides a pre-configured OkHttpClient for Google API calls.
     * Includes a logging interceptor for debugging and an auth header if needed.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BASIC
        }

        // Placeholder for OAuth token or API key injection.
        val authInterceptor = Interceptor { chain ->
            val request: Request = chain.request().newBuilder()
                // You can inject your OAuth Bearer token dynamically here.
                // Example: .addHeader("Authorization", "Bearer " + token)
                .build()
            chain.proceed(request)
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(authInterceptor)
            .build()
    }

    /**
     * Provides a Retrofit instance configured for Google Drive API.
     * Used by DriveSyncHelper for uploads, exports, and backups.
     */
    @Provides
    @Singleton
    fun provideDriveRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_DRIVE_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Provides a Retrofit instance configured for Google Calendar API.
     * Used for event syncing and scheduling reminders.
     */
    @Provides
    @Singleton
    fun provideCalendarRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GOOGLE_CALENDAR_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Initializes Firebase and provides an instance of FirebaseFirestore.
     * Firestore is used as Meadow’s online database for syncing projects, catalog items, etc.
     */
    @Provides
    @Singleton
    fun provideFirestore(context: Context): FirebaseFirestore {
        FirebaseApp.initializeApp(context)
        return FirebaseFirestore.getInstance()
    }

    /**
     * Provides FirebaseStorage instance for uploading large media assets (images, sounds, etc.)
     * Used by DriveSyncHelper and StorageHelper for hybrid cloud sync.
     */
    @Provides
    @Singleton
    fun provideFirebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}
