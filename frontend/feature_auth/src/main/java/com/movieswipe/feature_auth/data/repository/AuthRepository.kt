package com.movieswipe.feature_auth.data.repository

import com.movieswipe.feature_auth.data.model.AuthRequestDTO
import com.movieswipe.feature_auth.data.model.AuthResponseDTO
import com.movieswipe.feature_auth.data.remote.AuthApi
import retrofit2.Response
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val api: AuthApi
) {
    suspend fun authenticateWithGoogle(idToken: String): Response<AuthResponseDTO> {
        return api.authenticateWithGoogle(AuthRequestDTO(idToken))
    }
}

