package com.meadow.feature.script.di

import com.meadow.feature.common.ui.navigation.FeatureEntry
import com.meadow.feature.script.api.ScriptFeatureEntryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoSet

@Module
@InstallIn(SingletonComponent::class)
abstract class ScriptFeatureEntryModule {

    @Binds
    @IntoSet
    abstract fun bindScriptFeatureEntry(
        impl: ScriptFeatureEntryImpl
    ): FeatureEntry
}
