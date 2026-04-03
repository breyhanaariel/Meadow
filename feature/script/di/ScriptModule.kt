package com.meadow.feature.script.di

import android.content.Context
import androidx.room.Room
import com.meadow.feature.script.data.local.ScriptBlockAnchorDao
import com.meadow.feature.script.data.local.ScriptCatalogLinkDao
import com.meadow.feature.script.data.local.ScriptDao
import com.meadow.feature.script.data.local.ScriptDatabase
import com.meadow.feature.script.data.local.ScriptAssetDao
import com.meadow.feature.script.data.local.ScriptMediaTrackDao
import com.meadow.feature.script.data.local.ScriptVariantDao
import com.meadow.feature.script.data.local.SeasonDao
import com.meadow.feature.script.data.local.TranslationBlockStatusDao
import com.meadow.feature.script.data.repository.ScriptRepository
import com.meadow.feature.script.domain.repository.ScriptRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ScriptModule {

    @Binds
    abstract fun bindScriptRepository(
        repository: ScriptRepository
    ): ScriptRepositoryContract

    companion object {

        @Provides
        @Singleton
        fun provideScriptDatabase(
            @ApplicationContext context: Context
        ): ScriptDatabase =
            Room.databaseBuilder(
                context,
                ScriptDatabase::class.java,
                "scripts.db"
            )
                .fallbackToDestructiveMigration()
                .build()

        @Provides
        fun provideScriptDao(
            database: ScriptDatabase
        ): ScriptDao = database.scriptDao()

        @Provides
        fun provideSeasonDao(
            database: ScriptDatabase
        ): SeasonDao = database.seasonDao()

        @Provides
        fun provideScriptVariantDao(
            database: ScriptDatabase
        ): ScriptVariantDao = database.scriptVariantDao()

        @Provides
        fun provideScriptBlockAnchorDao(
            database: ScriptDatabase
        ): ScriptBlockAnchorDao = database.scriptBlockAnchorDao()

        @Provides
        fun provideScriptCatalogLinkDao(
            database: ScriptDatabase
        ): ScriptCatalogLinkDao = database.scriptCatalogLinkDao()

        @Provides
        fun provideTranslationBlockStatusDao(
            database: ScriptDatabase
        ): TranslationBlockStatusDao = database.translationBlockStatusDao()

        @Provides
        fun provideScriptAssetDao(
            database: ScriptDatabase
        ): ScriptAssetDao = database.scriptAssetDao()

        @Provides
        fun provideScriptMediaTrackDao(
            database: ScriptDatabase
        ): ScriptMediaTrackDao = database.scriptMediaTrackDao()
    }
}
