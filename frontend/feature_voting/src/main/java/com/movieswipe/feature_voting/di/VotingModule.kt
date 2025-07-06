package com.movieswipe.feature_voting.di

import com.movieswipe.feature_voting.data.remote.VotingApi
import com.movieswipe.feature_voting.data.repository.VotingRepository
import com.movieswipe.feature_voting.domain.usecase.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object VotingModule {
    @Provides
    @Singleton
    fun provideVotingApi(retrofit: Retrofit): VotingApi =
        retrofit.create(VotingApi::class.java)

    @Provides
    @Singleton
    fun provideVotingRepository(api: VotingApi): VotingRepository =
        VotingRepository(api)

    @Provides
    @Singleton
    fun provideSetPreferencesUseCase(repository: VotingRepository): SetPreferencesUseCase =
        SetPreferencesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetPreferencesUseCase(repository: VotingRepository): GetPreferencesUseCase =
        GetPreferencesUseCase(repository)

    @Provides
    @Singleton
    fun provideStartVotingSessionUseCase(repository: VotingRepository): StartVotingSessionUseCase =
        StartVotingSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideGetVotingSessionUseCase(repository: VotingRepository): GetVotingSessionUseCase =
        GetVotingSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideSubmitVoteUseCase(repository: VotingRepository): SubmitVoteUseCase =
        SubmitVoteUseCase(repository)

    @Provides
    @Singleton
    fun provideEndVotingSessionUseCase(repository: VotingRepository): EndVotingSessionUseCase =
        EndVotingSessionUseCase(repository)

    @Provides
    @Singleton
    fun provideGetVotingResultsUseCase(repository: VotingRepository): GetVotingResultsUseCase =
        GetVotingResultsUseCase(repository)
}

