package com.meadow.core.node.di

import com.meadow.core.node.data.NodeEdgeRepositoryImpl
import com.meadow.core.node.data.NodeRepositoryImpl
import com.meadow.core.node.repository.NodeEdgeRepository
import com.meadow.core.node.repository.NodeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NodeRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNodeRepository(impl: NodeRepositoryImpl): NodeRepository

    @Binds
    @Singleton
    abstract fun bindNodeEdgeRepository(impl: NodeEdgeRepositoryImpl): NodeEdgeRepository
}
