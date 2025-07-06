package com.movieswipe.feature_auth.domain.usecase

import com.movieswipe.feature_auth.data.model.AuthResponseDTO
import com.movieswipe.feature_auth.data.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class GoogleSignInUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(idToken: String): Response<AuthResponseDTO> {
        return repository.authenticateWithGoogle(idToken)
    }
}

