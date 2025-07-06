package com.example.movieswipe.domain.repository

import com.example.movieswipe.domain.model.User

// Repository interfaces for abstracting data operations
interface GroupRepository {
    // ...to be implemented
}

interface MovieRepository {
    // ...to be implemented
}

interface UserRepository {
    suspend fun authenticateWithGoogle(idToken: String): Result<Pair<String, User>>
    suspend fun updateProfile(token: String, name: String?, email: String?): Result<User>
    fun getCurrentUser(): User?
    fun getAuthToken(): String?
    fun logout()
}
