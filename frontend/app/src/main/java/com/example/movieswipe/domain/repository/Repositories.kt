package com.example.movieswipe.domain.repository

import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.User
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference

// Repository interfaces for abstracting data operations
interface GroupRepository {
    // ...to be implemented
}

interface MovieRepository {
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getMovie(tmdbId: Int): Result<Movie>
    suspend fun recommendMovie(userPreferences: List<UserPreference>): Result<MovieRecommendation>
}

interface UserRepository {
    suspend fun authenticateWithGoogle(idToken: String): Result<Pair<String, User>>
    suspend fun updateProfile(token: String, name: String?, email: String?): Result<User>
    fun getCurrentUser(): User?
    fun getAuthToken(): String?
    fun logout()
}
