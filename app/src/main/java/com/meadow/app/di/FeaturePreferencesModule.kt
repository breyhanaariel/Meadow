package com.meadow.app.di

import com.meadow.app.datastore.FeaturePreferences
import com.meadow.core.common.preferences.FeaturePreferencesProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FeaturePreferencesModule {

    @Binds
    @Singleton
    abstract fun bindFeaturePreferencesProvider(
        impl: FeaturePreferences
    ): FeaturePreferencesProvider
}
