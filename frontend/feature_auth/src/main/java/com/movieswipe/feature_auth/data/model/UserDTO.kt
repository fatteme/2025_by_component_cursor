package com.movieswipe.feature_auth.data.model

data class UserDTO(
    val id: String,
    val googleId: String,
    val name: String,
    val email: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class AuthRequestDTO(
    val idToken: String
)

data class AuthResponseDTO(
    val token: String,
    val user: UserDTO
)

data class ErrorResponseDTO(
    val error: String
)

