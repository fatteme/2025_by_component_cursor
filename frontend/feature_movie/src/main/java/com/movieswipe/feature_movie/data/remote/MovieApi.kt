package com.movieswipe.feature_movie.data.remote

import com.movieswipe.feature_movie.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MovieApi {
    @GET("/movies/genres")
    suspend fun getGenres(): Response<GenresResponseDTO>

    @GET("/movies/{id}")
    suspend fun getMovie(@Path("id") movieId: Int): Response<MovieResponseDTO>

    @POST("/movies/recommend")
    suspend fun getRecommendation(@Body request: RecommendationRequestDTO): Response<RecommendationResponseDTO>
}

