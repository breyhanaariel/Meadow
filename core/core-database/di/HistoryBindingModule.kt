package com.meadow.core.database.di

import com.meadow.core.database.history.HistoryDatabaseAdapter
import com.meadow.core.domain.repository.HistoryRepositoryContract
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HistoryBindingModule {

    @Binds
    @Singleton
    abstract fun bindHistoryRepositoryContract(
        impl: HistoryDatabaseAdapter
    ): HistoryRepositoryContract
}