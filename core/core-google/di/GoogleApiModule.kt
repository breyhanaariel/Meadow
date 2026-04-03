package com.meadow.core.google.di

import com.google.gson.Gson
import com.meadow.core.google.api.calendar.CalendarApi
import com.meadow.core.google.api.docs.DocsApi
import com.meadow.core.google.api.drive.DriveApi
import com.meadow.core.google.api.sheets.SheetsApi
import com.meadow.core.google.api.translate.TranslateApi
import com.meadow.core.network.di.GoogleRetrofit
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleApiModule {

    @Provides
    @Singleton
    @GoogleRetrofit
    fun provideGoogleRetrofit(
        okHttpClient: OkHttpClient,
        gson: Gson
    ): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

    @Provides
    @Singleton
    fun provideDriveApi(
        @GoogleRetrofit retrofit: Retrofit
    ): DriveApi =
        retrofit.create(DriveApi::class.java)

    @Provides
    @Singleton
    fun provideSheetsApi(
        @GoogleRetrofit retrofit: Retrofit
    ): SheetsApi =
        retrofit.create(SheetsApi::class.java)

    @Provides
    @Singleton
    fun provideDocsApi(
        @GoogleRetrofit retrofit: Retrofit
    ): DocsApi =
        retrofit.create(DocsApi::class.java)

    @Provides
    @Singleton
    fun provideCalendarApi(
        @GoogleRetrofit retrofit: Retrofit
    ): CalendarApi =
        retrofit.create(CalendarApi::class.java)

    @Provides
    @Singleton
    fun provideTranslateApi(
        @GoogleRetrofit retrofit: Retrofit
    ): TranslateApi =
        retrofit.create(TranslateApi::class.java)
}
