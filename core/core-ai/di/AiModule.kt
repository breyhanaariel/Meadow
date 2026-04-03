package com.meadow.core.ai.di

import android.R
import android.content.Context
import androidx.room.Room
import com.meadow.core.ai.BuildConfig
import com.meadow.core.ai.engine.manager.AiManager
import com.meadow.core.ai.engine.personas.bloom.BloomUseCases
import com.meadow.core.ai.engine.personas.bud.BudUseCases
import com.meadow.core.ai.engine.personas.meadow.MeadowUseCases
import com.meadow.core.ai.engine.personas.petal.PetalUseCases
import com.meadow.core.ai.engine.personas.sprout.SproutUseCases
import com.meadow.core.ai.engine.personas.vine.VineUseCases
import com.meadow.core.ai.engine.prompt.AiPromptProvider
import com.meadow.core.ai.data.chat.AiChatRepository
import com.meadow.core.ai.data.context.AiContextDao
import com.meadow.core.ai.data.context.AiContextRepository
import com.meadow.core.ai.data.remote.RemoteChatDataSource
import com.meadow.core.ai.data.repository.AiRepository
import com.meadow.core.ai.data.sync.ChatSyncCoordinator
import com.meadow.core.ai.domain.contracts.AiRepositoryContract
import com.meadow.core.ai.domain.contracts.PdfRepositoryContract
import com.meadow.core.ai.gemini.GeminiClient
import com.meadow.core.ai.pdf.BookmarkRepository
import com.meadow.core.ai.pdf.PdfBookmarkDao
import com.meadow.core.ai.pdf.PdfBookmarkDatabase
import com.meadow.core.ai.pdf.PdfRepository
import com.meadow.core.ai.pdf.reference.ReferenceDao
import com.meadow.core.ai.pdf.reference.ReferenceDatabase
import com.meadow.core.ai.pdf.reference.ReferenceRetriever
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AiModule {

    /* ───────── PROMPT + CORE REPOSITORY ───────── */

    @Provides
    @Singleton
    fun provideAiPromptProvider(
        @ApplicationContext context: Context
    ): AiPromptProvider = AiPromptProvider(context)

    @Provides
    @Singleton
    fun provideAiRepository(
        promptProvider: AiPromptProvider,
        geminiClient: GeminiClient,
        @ApplicationContext context: Context
    ): AiRepositoryContract {

        val intStrings: (Int) -> String = { id ->
            context.getString(id)
        }

        val keyStrings: (String) -> String = { key ->
            val resId = context.resources.getIdentifier(
                key,
                "string",
                context.packageName
            )
            if (resId != 0) context.getString(resId) else key
        }

        return AiRepository(
            geminiClient = geminiClient,
            promptProvider = promptProvider,
            intStrings = intStrings,
            keyStrings = keyStrings
        )
    }

    /* ───────── CONTEXT REPOSITORY (DAO from AiStorageModule) ───────── */

    @Provides
    @Singleton
    fun provideAiContextRepository(
        dao: AiContextDao
    ): AiContextRepository =
        AiContextRepository(dao)

    /* ───────── PDF BOOKMARK STORAGE ───────── */

    @Provides
    @Singleton
    fun providePdfRepository(
        @ApplicationContext context: Context
    ): PdfRepositoryContract = PdfRepository(context)

    @Provides
    @Singleton
    fun providePdfBookmarkDatabase(
        @ApplicationContext context: Context
    ): PdfBookmarkDatabase =
        Room.databaseBuilder(
            context,
            PdfBookmarkDatabase::class.java,
            "pdf_bookmarks.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun providePdfBookmarkDao(
        db: PdfBookmarkDatabase
    ): PdfBookmarkDao = db.bookmarkDao()

    @Provides
    @Singleton
    fun provideBookmarkRepository(
        dao: PdfBookmarkDao
    ): BookmarkRepository = BookmarkRepository(dao)

    /* ───────── REFERENCE STORAGE ───────── */

    @Provides
    @Singleton
    fun provideReferenceDatabase(
        @ApplicationContext context: Context
    ): ReferenceDatabase =
        Room.databaseBuilder(
            context,
            ReferenceDatabase::class.java,
            "pdf_reference.db"
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @Singleton
    fun provideReferenceDao(
        db: ReferenceDatabase
    ): ReferenceDao = db.referenceDao()

    @Provides
    @Singleton
    fun provideReferenceRetriever(
        dao: ReferenceDao
    ): ReferenceRetriever =
        ReferenceRetriever(dao)

    /* ───────── PERSONA USE CASES ───────── */

    @Provides
    @Singleton
    fun provideBudUseCases(
        contextRepo: AiContextRepository,
        geminiClient: GeminiClient
    ): BudUseCases =
        BudUseCases(contextRepo, geminiClient)

    @Provides
    @Singleton
    fun provideSproutUseCases(
        repo: AiRepositoryContract,
        contextRepo: AiContextRepository
    ): SproutUseCases =
        SproutUseCases(repo, contextRepo)

    @Provides
    @Singleton
    fun provideBloomUseCases(
        repo: AiRepositoryContract,
        contextRepo: AiContextRepository
    ): BloomUseCases =
        BloomUseCases(repo, contextRepo)

    @Provides
    @Singleton
    fun providePetalUseCases(
        repo: AiRepositoryContract,
        contextRepo: AiContextRepository
    ): PetalUseCases =
        PetalUseCases(repo, contextRepo)

    @Provides
    @Singleton
    fun provideVineUseCases(
        repo: AiRepositoryContract,
        contextRepo: AiContextRepository
    ): VineUseCases =
        VineUseCases(repo, contextRepo)

    @Provides
    @Singleton
    fun provideMeadowUseCases(
        repo: AiRepositoryContract,
        referenceRetriever: ReferenceRetriever,
        contextRepo: AiContextRepository
    ): MeadowUseCases =
        MeadowUseCases(
            aiRepository = repo,
            referenceRetriever = referenceRetriever,
            contextRepo = contextRepo
        )

    /* ───────── AI MANAGER ───────── */

    @Provides
    @Singleton
    fun provideAiManager(
        bud: BudUseCases,
        sprout: SproutUseCases,
        bloom: BloomUseCases,
        petal: PetalUseCases,
        vine: VineUseCases,
        meadow: MeadowUseCases
    ): AiManager =
        AiManager(
            bud = bud,
            sprout = sprout,
            bloom = bloom,
            petal = petal,
            vine = vine,
            meadow = meadow
        )

    /* ───────── CHAT SYNC ───────── */

    @Provides
    @Singleton
    fun provideChatSyncCoordinator(
        chatRepo: AiChatRepository,
        remote: RemoteChatDataSource
    ): ChatSyncCoordinator =
        ChatSyncCoordinator(chatRepo, remote)



}
