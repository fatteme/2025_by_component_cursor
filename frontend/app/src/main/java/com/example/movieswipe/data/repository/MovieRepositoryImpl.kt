package com.example.movieswipe.data.repository

import com.example.movieswipe.data.remote.MovieApiService
import com.example.movieswipe.data.remote.RecommendationRequest
import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference
import com.example.movieswipe.domain.repository.MovieRepository
import javax.inject.Inject

class MovieRepositoryImpl @Inject constructor(
    private val api: MovieApiService
) : MovieRepository {
    override suspend fun getGenres(): Result<List<Genre>> = try {
        val response = api.getGenres()
        if (response.isSuccessful) {
            Result.success(response.body()?.genres ?: emptyList())
        } else {
            Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch genres"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun getMovie(tmdbId: Int): Result<Movie> = try {
        val response = api.getMovie(tmdbId)
        if (response.isSuccessful) {
            response.body()?.movie?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty movie response"))
        } else {
            Result.failure(Exception(response.errorBody()?.string() ?: "Failed to fetch movie"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }

    override suspend fun recommendMovie(userPreferences: List<UserPreference>): Result<MovieRecommendation> = try {
        val response = api.recommendMovie(RecommendationRequest(userPreferences))
        if (response.isSuccessful) {
            response.body()?.recommendation?.let { Result.success(it) }
                ?: Result.failure(Exception("Empty recommendation response"))
        } else {
            Result.failure(Exception(response.errorBody()?.string() ?: "Failed to recommend movie"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

