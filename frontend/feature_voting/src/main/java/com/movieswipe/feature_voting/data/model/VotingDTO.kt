package com.movieswipe.feature_voting.data.model

data class UserPreferenceDTO(
    val id: String?,
    val userId: String,
    val groupId: String,
    val preferredGenres: List<Int>,
    val createdAt: String?,
    val updatedAt: String?
)

data class SetPreferencesRequestDTO(
    val preferredGenres: List<Int>
)

data class PreferenceResponseDTO(
    val preference: UserPreferenceDTO
)

data class PreferencesResponseDTO(
    val preferences: List<UserPreferenceDTO>
)

data class VotingSessionDTO(
    val id: String,
    val groupId: String,
    val movies: List<Int>,
    val isActive: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

data class VotingSessionResponseDTO(
    val votingSession: VotingSessionDTO
)

data class SubmitVoteRequestDTO(
    val movieId: Int,
    val vote: String // "yes" or "no"
)

data class VotingResultDTO(
    val movieId: Int,
    val yesVotes: Int,
    val noVotes: Int,
    val totalVotes: Int,
    val score: Float
)

data class VotingResultsResponseDTO(
    val results: ResultsDetailDTO
)

data class ResultsDetailDTO(
    val sessionId: String,
    val isActive: Boolean,
    val movies: List<Int>,
    val results: List<VotingResultDTO>,
    val bestMatch: VotingResultDTO?
)

