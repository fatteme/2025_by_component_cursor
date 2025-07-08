package com.example.movieswipe.ui.voting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.VotingResult
import com.example.movieswipe.domain.usecase.StartVotingSessionUseCase
import com.example.movieswipe.domain.usecase.EndVotingSessionUseCase
import com.example.movieswipe.domain.usecase.GetVotingSessionUseCase
import com.example.movieswipe.domain.usecase.SubmitVoteUseCase
import com.example.movieswipe.domain.usecase.GetVotingResultUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VotingViewModel @Inject constructor(
    private val startVotingSessionUseCase: StartVotingSessionUseCase,
    private val endVotingSessionUseCase: EndVotingSessionUseCase,
    private val getVotingSessionUseCase: GetVotingSessionUseCase,
    private val submitVoteUseCase: SubmitVoteUseCase,
    private val getVotingResultUseCase: GetVotingResultUseCase
) : ViewModel() {
    private val _session = MutableStateFlow<VotingSession?>(null)
    val session: StateFlow<VotingSession?> = _session

    private val _result = MutableStateFlow<VotingResult?>(null)
    val result: StateFlow<VotingResult?> = _result

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun startVotingSession(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = startVotingSessionUseCase(groupId)
            _loading.value = false
            result.onSuccess { _session.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun getVotingSession(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = getVotingSessionUseCase(groupId)
            _loading.value = false
            result.onSuccess { _session.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun submitVote(groupId: String, movieTmdbId: Int, vote: Boolean) {
        viewModelScope.launch {
            _loading.value = true
            val result = submitVoteUseCase(groupId, movieTmdbId, vote)
            _loading.value = false
            result.onFailure { _error.value = it.message }
        }
    }

    fun endVotingSession(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = endVotingSessionUseCase(groupId)
            _loading.value = false
            result.onSuccess { _result.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun getVotingResult(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = getVotingResultUseCase(groupId)
            _loading.value = false
            result.onSuccess { _result.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun clearResult() { _result.value = null }
}

