package com.example.movieswipe.di

import android.content.Context
import android.content.SharedPreferences
import com.example.movieswipe.data.remote.MovieApiService
import com.example.movieswipe.data.remote.UserApiService
import com.example.movieswipe.data.remote.GroupApiService
import com.example.movieswipe.data.repository.MovieRepositoryImpl
import com.example.movieswipe.data.repository.UserRepositoryImpl
import com.example.movieswipe.data.repository.GroupRepositoryImpl
import com.example.movieswipe.domain.repository.MovieRepository
import com.example.movieswipe.domain.repository.UserRepository
import com.example.movieswipe.domain.repository.GroupRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

// Hilt modules for providing dependencies
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.movieswipe.com/") // Use production by default
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideUserApiService(retrofit: Retrofit): UserApiService =
        retrofit.create(UserApiService::class.java)

    @Provides
    @Singleton
    fun provideMovieApiService(retrofit: Retrofit): MovieApiService =
        retrofit.create(MovieApiService::class.java)

    @Provides
    @Singleton
    fun provideGroupApiService(retrofit: Retrofit): GroupApiService =
        retrofit.create(GroupApiService::class.java)

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("movieswipe_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserRepository(
        api: UserApiService,
        prefs: SharedPreferences
    ): UserRepository = UserRepositoryImpl(api, prefs)

    @Provides
    @Singleton
    fun provideMovieRepository(
        api: MovieApiService
    ): MovieRepository = MovieRepositoryImpl(api)

    @Provides
    @Singleton
    fun provideGroupRepository(
        api: GroupApiService
    ): GroupRepository = GroupRepositoryImpl(api)
}
