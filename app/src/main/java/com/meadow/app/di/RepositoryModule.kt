package com.meadow.app.di

/** import com.meadow.feature.project.data.repository.ProjectRepository
import com.meadow.feature.project.domain.repository.ProjectRepositoryContract

import com.meadow.feature.catalog.repository.CatalogRepository
import com.meadow.feature.catalog.repository.CatalogRepositoryContract

import com.meadow.feature.timeline.repository.TimelineRepository
import com.meadow.feature.timeline.repository.TimelineRepositoryContract

import com.meadow.feature.script.data.repository.ScriptRepository
import com.meadow.feature.script.domain.repository.ScriptRepositoryContract

import com.meadow.feature.wiki.repository.WikiRepository
import com.meadow.feature.wiki.repository.WikiRepositoryContract

import com.meadow.feature.familytree.repository.FamilyTreeRepository
import com.meadow.feature.familytree.repository.FamilyTreeRepositoryContract


import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton abstract fun bindProjectRepository(
        impl: ProjectRepository
    ): ProjectRepositoryContract

    @Binds @Singleton abstract fun bindCatalogRepository(
        impl: CatalogRepository
    ): CatalogRepositoryContract

    @Binds @Singleton abstract fun bindScriptRepository(
        impl: ScriptRepository
    ): ScriptRepositoryContract

    @Binds @Singleton abstract fun bindTimelineRepository(
        impl: TimelineRepository
    ): TimelineRepositoryContract

    @Binds @Singleton abstract fun bindFamilyTreeRepository(
        impl: FamilyTreeRepository
    ): FamilyTreeRepositoryContract

    @Binds @Singleton abstract fun bindWikiRepository(
        impl: WikiRepository
    ): WikiRepositoryContract

}
**/