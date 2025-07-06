package com.example.movieswipe.data.remote

import com.example.movieswipe.domain.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT

// Data classes for API requests/responses
data class AuthRequest(val idToken: String)
data class AuthResponse(val token: String, val user: User)
data class UpdateUserRequest(val name: String?, val email: String?)
data class UpdateUserResponse(val user: User)
data class ErrorResponse(val error: String)

// Remote data sources and API service definitions
interface MovieApiService {
    // ...to be implemented
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
