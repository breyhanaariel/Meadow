package com.meadow.core.auth.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.meadow.core.auth.api.AuthManager
import com.meadow.core.auth.api.GoogleTokenProvider
import com.meadow.core.auth.firebase.FirebaseAuthManager
import com.meadow.core.google.engine.GoogleSignInManager
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthModule {

    @Binds
    @Singleton
    abstract fun bindAuthManager(
        impl: FirebaseAuthManager
    ): AuthManager

    companion object {

        @Provides
        @Singleton
        fun provideFirebaseAuth(): FirebaseAuth =
            FirebaseAuth.getInstance()

        @Provides
        @Singleton
        fun provideGoogleTokenProvider(
            signInManager: GoogleSignInManager
        ): GoogleTokenProvider =
            object : GoogleTokenProvider {

                override fun idToken(context: Context): String? =
                    signInManager.idToken()

                override suspend fun signOut(context: Context) {
                    signInManager.signOut(context)
                }

                override suspend fun revokeAccess(context: Context) {
                    signInManager.revokeAccess(context)
                }
            }
    }
}
