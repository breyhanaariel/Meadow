package com.meadow.core.node.di

import android.content.Context
import androidx.room.Room
import com.meadow.core.node.data.NodeDatabase
import com.meadow.core.node.data.NodeDao
import com.meadow.core.node.data.NodeEdgeDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NodeStorageModule {

    @Provides
    @Singleton
    fun provideNodeDatabase(@ApplicationContext ctx: Context): NodeDatabase =
        Room.databaseBuilder(ctx, NodeDatabase::class.java, "core_nodes.db")
            .build()

    @Provides
    fun provideNodeDao(db: NodeDatabase): NodeDao = db.nodeDao()

    @Provides
    fun provideNodeEdgeDao(db: NodeDatabase): NodeEdgeDao = db.nodeEdgeDao()
}
