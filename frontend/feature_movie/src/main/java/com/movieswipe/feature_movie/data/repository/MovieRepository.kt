package com.movieswipe.feature_movie.data.repository

import com.movieswipe.feature_movie.data.model.*
import com.movieswipe.feature_movie.data.remote.MovieApi
import retrofit2.Response
import javax.inject.Inject

class MovieRepository @Inject constructor(
    private val api: MovieApi
) {
    suspend fun getGenres(): Response<GenresResponseDTO> = api.getGenres()
    suspend fun getMovie(movieId: Int): Response<MovieResponseDTO> = api.getMovie(movieId)
    suspend fun getRecommendation(request: RecommendationRequestDTO): Response<RecommendationResponseDTO> = api.getRecommendation(request)
}

