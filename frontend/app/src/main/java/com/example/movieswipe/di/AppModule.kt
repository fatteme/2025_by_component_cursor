package com.example.movieswipe.di

import android.content.Context
import android.content.SharedPreferences
import com.example.movieswipe.data.remote.UserApiService
import com.example.movieswipe.data.repository.UserRepositoryImpl
import com.example.movieswipe.domain.repository.UserRepository
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
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("movieswipe_prefs", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideUserRepository(
        api: UserApiService,
        prefs: SharedPreferences
    ): UserRepository = UserRepositoryImpl(api, prefs)
}
