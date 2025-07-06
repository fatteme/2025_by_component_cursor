ceopackage com.movieswipe.feature_voting.data.repository

import com.movieswipe.feature_voting.data.model.*
import com.movieswipe.feature_voting.data.remote.VotingApi
import retrofit2.Response
import javax.inject.Inject

class VotingRepository @Inject constructor(
    private val api: VotingApi
) {
    suspend fun setPreferences(groupId: String, genres: List<Int>): Response<PreferenceResponseDTO> =
        api.setPreferences(groupId, SetPreferencesRequestDTO(genres))

    suspend fun getPreferences(groupId: String): Response<PreferencesResponseDTO> =
        api.getPreferences(groupId)

    suspend fun startVotingSession(groupId: String): Response<VotingSessionResponseDTO> =
        api.startVotingSession(groupId)

    suspend fun getVotingSession(sessionId: String): Response<VotingSessionResponseDTO> =
        api.getVotingSession(sessionId)

    suspend fun submitVote(sessionId: String, movieId: Int, vote: String): Response<VotingSessionResponseDTO> =
        api.submitVote(sessionId, SubmitVoteRequestDTO(movieId, vote))

    suspend fun endVotingSession(sessionId: String): Response<VotingResultsResponseDTO> =
        api.endVotingSession(sessionId)

    suspend fun getVotingResults(sessionId: String): Response<VotingResultsResponseDTO> =
        api.getVotingResults(sessionId)
}

