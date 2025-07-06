package com.movieswipe.feature_voting.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieswipe.feature_voting.data.model.PreferenceResponseDTO
import com.movieswipe.feature_voting.data.model.PreferencesResponseDTO
import com.movieswipe.feature_voting.data.model.VotingResultsResponseDTO
import com.movieswipe.feature_voting.data.model.VotingSessionResponseDTO
import com.movieswipe.feature_voting.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

sealed class VotingUiState {
    object Idle : VotingUiState()
    object Loading : VotingUiState()
    data class PreferencesSet(val response: PreferenceResponseDTO) : VotingUiState()
    data class PreferencesLoaded(val response: PreferencesResponseDTO) : VotingUiState()
    data class VotingSessionStarted(val response: VotingSessionResponseDTO) : VotingUiState()
    data class VotingSessionLoaded(val response: VotingSessionResponseDTO) : VotingUiState()
    data class VoteSubmitted(val response: VotingSessionResponseDTO) : VotingUiState()
    data class VotingEnded(val response: VotingResultsResponseDTO) : VotingUiState()
    data class VotingResultsLoaded(val response: VotingResultsResponseDTO) : VotingUiState()
    data class Error(val message: String) : VotingUiState()
}

@HiltViewModel
class VotingViewModel @Inject constructor(
    private val setPreferencesUseCase: SetPreferencesUseCase,
    private val getPreferencesUseCase: GetPreferencesUseCase,
    private val startVotingSessionUseCase: StartVotingSessionUseCase,
    private val getVotingSessionUseCase: GetVotingSessionUseCase,
    private val submitVoteUseCase: SubmitVoteUseCase,
    private val endVotingSessionUseCase: EndVotingSessionUseCase,
    private val getVotingResultsUseCase: GetVotingResultsUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<VotingUiState>(VotingUiState.Idle)
    val uiState: StateFlow<VotingUiState> = _uiState

    fun setPreferences(groupId: String, genres: List<Int>) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = setPreferencesUseCase(groupId, genres)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.PreferencesSet(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun getPreferences(groupId: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = getPreferencesUseCase(groupId)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.PreferencesLoaded(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun startVotingSession(groupId: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = startVotingSessionUseCase(groupId)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.VotingSessionStarted(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun getVotingSession(sessionId: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = getVotingSessionUseCase(sessionId)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.VotingSessionLoaded(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun submitVote(sessionId: String, movieId: Int, vote: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = submitVoteUseCase(sessionId, movieId, vote)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.VoteSubmitted(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun endVotingSession(sessionId: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = endVotingSessionUseCase(sessionId)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.VotingEnded(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun getVotingResults(sessionId: String) {
        _uiState.value = VotingUiState.Loading
        viewModelScope.launch {
            val response = getVotingResultsUseCase(sessionId)
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = VotingUiState.VotingResultsLoaded(response.body()!!)
            } else {
                _uiState.value = VotingUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }
}

