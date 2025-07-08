package com.example.movieswipe.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieswipe.domain.model.User
import com.example.movieswipe.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user

    private val _authToken = MutableStateFlow<String?>(null)
    val authToken: StateFlow<String?> = _authToken

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun authenticateWithGoogle(idToken: String) {
        viewModelScope.launch {
            val result = userRepository.authenticateWithGoogle(idToken)
            result.onSuccess { (token, user) ->
                _authToken.value = token
                _user.value = user
                _error.value = null
            }.onFailure {
                _error.value = it.message
            }
        }
    }

    fun logout() {
        userRepository.logout()
        _user.value = null
        _authToken.value = null
    }

    fun loadSession() {
        _user.value = userRepository.getCurrentUser()
        _authToken.value = userRepository.getAuthToken()
    }
}

