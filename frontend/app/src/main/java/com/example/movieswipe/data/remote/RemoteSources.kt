package com.example.movieswipe.data.remote

import com.example.movieswipe.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Data classes for API requests/responses
data class AuthRequest(val idToken: String)
data class AuthResponse(val token: String, val user: User)
data class UpdateUserRequest(val name: String?, val email: String?)
data class UpdateUserResponse(val user: User)
data class GenresResponse(val genres: List<com.example.movieswipe.domain.model.Genre>)
data class MovieResponse(val movie: com.example.movieswipe.domain.model.Movie)
data class RecommendationRequest(val userPreferences: List<com.example.movieswipe.domain.model.UserPreference>)
data class RecommendationResponse(val recommendation: com.example.movieswipe.domain.model.MovieRecommendation)
data class ErrorResponse(val error: String)

// Remote data sources and API service definitions
interface MovieApiService {
    @GET("/movies/genres")
    suspend fun getGenres(): retrofit2.Response<GenresResponse>

    @GET("/movies/{id}")
    suspend fun getMovie(@Path("id") tmdbId: Int): retrofit2.Response<MovieResponse>

    @POST("/movies/recommend")
    suspend fun recommendMovie(@Body request: RecommendationRequest): retrofit2.Response<RecommendationResponse>
}

interface GroupApiService {
    // ...to be implemented
}

interface UserApiService {
    @POST("/auth/google")
    suspend fun authenticateWithGoogle(@Body request: AuthRequest): Response<AuthResponse>

    @PUT("/users/me")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserRequest
    ): Response<UpdateUserResponse>
}
