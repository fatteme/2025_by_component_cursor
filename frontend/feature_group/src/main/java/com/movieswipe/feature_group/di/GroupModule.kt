package com.movieswipe.feature_group.di

import com.movieswipe.feature_group.data.remote.GroupApi
import com.movieswipe.feature_group.data.repository.GroupRepository
import com.movieswipe.feature_group.domain.usecase.CreateGroupUseCase
import com.movieswipe.feature_group.domain.usecase.DeleteGroupUseCase
import com.movieswipe.feature_group.domain.usecase.GetGroupUseCase
import com.movieswipe.feature_group.domain.usecase.GetGroupsUseCase
import com.movieswipe.feature_group.domain.usecase.JoinGroupUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GroupModule {
    @Provides
    @Singleton
    fun provideGroupApi(retrofit: Retrofit): GroupApi =
        retrofit.create(GroupApi::class.java)

    @Provides
    @Singleton
    fun provideGroupRepository(api: GroupApi): GroupRepository =
        GroupRepository(api)

    @Provides
    @Singleton
    fun provideCreateGroupUseCase(repository: GroupRepository): CreateGroupUseCase =
        CreateGroupUseCase(repository)

    @Provides
    @Singleton
    fun provideGetGroupsUseCase(repository: GroupRepository): GetGroupsUseCase =
        GetGroupsUseCase(repository)

    @Provides
    @Singleton
    fun provideJoinGroupUseCase(repository: GroupRepository): JoinGroupUseCase =
        JoinGroupUseCase(repository)

    @Provides
    @Singleton
    fun provideGetGroupUseCase(repository: GroupRepository): GetGroupUseCase =
        GetGroupUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteGroupUseCase(repository: GroupRepository): DeleteGroupUseCase =
        DeleteGroupUseCase(repository)
}

