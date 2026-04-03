package com.meadow.feature.common.di

import com.meadow.feature.common.api.FeatureContextProvider
import com.meadow.feature.common.state.FeatureContextState
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FeatureCommonModule {

    @Provides
    @Singleton
    fun provideFeatureContextState(): FeatureContextState =
        FeatureContextState()

    @Provides
    @Singleton
    fun provideFeatureContextProvider(
        state: FeatureContextState
    ): FeatureContextProvider = object : FeatureContextProvider {
        override val context = state.context
    }
}
