package com.movieswipe.feature_auth.data.remote

import com.movieswipe.feature_auth.data.model.AuthRequestDTO
import com.movieswipe.feature_auth.data.model.AuthResponseDTO
import com.movieswipe.feature_auth.data.model.ErrorResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("/auth/google")
    suspend fun authenticateWithGoogle(
        @Body request: AuthRequestDTO
    ): Response<AuthResponseDTO>
}

