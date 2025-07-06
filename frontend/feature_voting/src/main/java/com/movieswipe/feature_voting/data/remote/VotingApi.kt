package com.movieswipe.feature_voting.data.remote

import com.movieswipe.feature_voting.data.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VotingApi {
    @POST("/groups/{id}/preferences")
    suspend fun setPreferences(
        @Path("id") groupId: String,
        @Body request: SetPreferencesRequestDTO
    ): Response<PreferenceResponseDTO>

    @GET("/groups/{id}/preferences")
    suspend fun getPreferences(
        @Path("id") groupId: String
    ): Response<PreferencesResponseDTO>

    @POST("/groups/{id}/voting-sessions")
    suspend fun startVotingSession(
        @Path("id") groupId: String
    ): Response<VotingSessionResponseDTO>

    @GET("/groups/voting-sessions/{sessionId}")
    suspend fun getVotingSession(
        @Path("sessionId") sessionId: String
    ): Response<VotingSessionResponseDTO>

    @POST("/groups/voting-sessions/{sessionId}/votes")
    suspend fun submitVote(
        @Path("sessionId") sessionId: String,
        @Body request: SubmitVoteRequestDTO
    ): Response<VotingSessionResponseDTO>

    @PUT("/groups/voting-sessions/{sessionId}/end")
    suspend fun endVotingSession(
        @Path("sessionId") sessionId: String
    ): Response<VotingResultsResponseDTO>

    @GET("/groups/voting-sessions/{sessionId}/results")
    suspend fun getVotingResults(
        @Path("sessionId") sessionId: String
    ): Response<VotingResultsResponseDTO>
}

