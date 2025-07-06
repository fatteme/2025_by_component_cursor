package com.movieswipe.feature_auth.di

import com.movieswipe.feature_auth.data.remote.AuthApi
import com.movieswipe.feature_auth.data.repository.AuthRepository
import com.movieswipe.feature_auth.domain.usecase.GoogleSignInUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {
    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideAuthRepository(api: AuthApi): AuthRepository =
        AuthRepository(api)

    @Provides
    @Singleton
    fun provideGoogleSignInUseCase(repository: AuthRepository): GoogleSignInUseCase =
        GoogleSignInUseCase(repository)
}

