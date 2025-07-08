package com.example.movieswipe.data.repository

import android.content.SharedPreferences
import com.example.movieswipe.data.remote.AuthRequest
import com.example.movieswipe.data.remote.UserApiService
import com.example.movieswipe.data.remote.UpdateUserRequest
import com.example.movieswipe.domain.model.User
import com.example.movieswipe.domain.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val api: UserApiService,
    private val prefs: SharedPreferences
) : UserRepository {
    private var cachedUser: User? = null
    private var cachedToken: String? = null

    override fun getCurrentUser(): User? = cachedUser
    override fun getAuthToken(): String? = cachedToken

    override suspend fun authenticateWithGoogle(idToken: String): Result<Pair<String, User>> = withContext(Dispatchers.IO) {
        try {
            val response = api.authenticateWithGoogle(AuthRequest(idToken))
            if (response.isSuccessful) {
                val body = response.body() ?: return@withContext Result.failure(Exception("Empty response"))
                cachedUser = body.user
                cachedToken = body.token
                prefs.edit().putString(KEY_TOKEN, body.token).apply()
                // Optionally cache user info
                Result.success(body.token to body.user)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Auth failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProfile(token: String, name: String?, email: String?): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.updateProfile("Bearer $token", UpdateUserRequest(name, email))
            if (response.isSuccessful) {
                val user = response.body()?.user ?: return@withContext Result.failure(Exception("Empty response"))
                cachedUser = user
                Result.success(user)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Update failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun logout() {
        cachedUser = null
        cachedToken = null
        prefs.edit().remove(KEY_TOKEN).apply()
    }

    companion object {
        private const val KEY_TOKEN = "auth_token"
    }
}

