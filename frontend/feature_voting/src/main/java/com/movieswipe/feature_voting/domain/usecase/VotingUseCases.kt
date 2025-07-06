package com.movieswipe.feature_voting.domain.usecase

import com.movieswipe.feature_voting.data.model.*
import com.movieswipe.feature_voting.data.repository.VotingRepository
import retrofit2.Response
import javax.inject.Inject

class SetPreferencesUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(groupId: String, genres: List<Int>): Response<PreferenceResponseDTO> =
        repository.setPreferences(groupId, genres)
}

class GetPreferencesUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(groupId: String): Response<PreferencesResponseDTO> =
        repository.getPreferences(groupId)
}

class StartVotingSessionUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(groupId: String): Response<VotingSessionResponseDTO> =
        repository.startVotingSession(groupId)
}

class GetVotingSessionUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(sessionId: String): Response<VotingSessionResponseDTO> =
        repository.getVotingSession(sessionId)
}

class SubmitVoteUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(sessionId: String, movieId: Int, vote: String): Response<VotingSessionResponseDTO> =
        repository.submitVote(sessionId, movieId, vote)
}

class EndVotingSessionUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(sessionId: String): Response<VotingResultsResponseDTO> =
        repository.endVotingSession(sessionId)
}

class GetVotingResultsUseCase @Inject constructor(private val repository: VotingRepository) {
    suspend operator fun invoke(sessionId: String): Response<VotingResultsResponseDTO> =
        repository.getVotingResults(sessionId)
}

