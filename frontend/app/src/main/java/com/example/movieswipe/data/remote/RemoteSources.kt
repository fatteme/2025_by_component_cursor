package com.example.movieswipe.data.remote

import com.example.movieswipe.domain.model.User
import com.example.movieswipe.domain.model.Group
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.Vote
import com.example.movieswipe.domain.model.VotingResult
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

// Data classes for API requests/responses
data class AuthRequest(val idToken: String)
data class AuthResponse(val token: String, val user: User)
data class UpdateUserRequest(val name: String?, val email: String?)
data class UpdateUserResponse(val user: User)
data class GenresResponse(val genres: List<com.example.movieswipe.domain.model.Genre>)
data class MovieResponse(val movie: com.example.movieswipe.domain.model.Movie)
data class RecommendationRequest(val userPreferences: List<com.example.movieswipe.domain.model.UserPreference>)
data class RecommendationResponse(val recommendation: com.example.movieswipe.domain.model.MovieRecommendation)
data class ErrorResponse(val error: String)

// Group API requests/responses
data class CreateGroupRequest(val name: String)
data class CreateGroupResponse(val group: Group)
data class JoinGroupRequest(val invitationCode: String)
data class JoinGroupResponse(val group: Group)
data class SetPreferencesRequest(val preferredGenres: List<Int>)
data class StartVotingSessionResponse(val session: VotingSession)
data class SubmitVoteRequest(val movieTmdbId: Int, val vote: Boolean)
data class VotingResultResponse(val result: VotingResult)

// Remote data sources and API service definitions
interface MovieApiService {
    @GET("/movies/genres")
    suspend fun getGenres(): retrofit2.Response<GenresResponse>

    @GET("/movies/{id}")
    suspend fun getMovie(@Path("id") tmdbId: Int): retrofit2.Response<MovieResponse>

    @POST("/movies/recommend")
    suspend fun recommendMovie(@Body request: RecommendationRequest): retrofit2.Response<RecommendationResponse>
}

interface GroupApiService {
    @POST("/groups")
    suspend fun createGroup(@Body request: CreateGroupRequest): retrofit2.Response<CreateGroupResponse>

    @POST("/groups/join")
    suspend fun joinGroup(@Body request: JoinGroupRequest): retrofit2.Response<JoinGroupResponse>

    @GET("/groups/{id}")
    suspend fun getGroup(@Path("id") groupId: String): retrofit2.Response<CreateGroupResponse>

    @POST("/groups/{id}/preferences")
    suspend fun setPreferences(
        @Path("id") groupId: String,
        @Body request: SetPreferencesRequest
    ): retrofit2.Response<Unit>

    @POST("/groups/{id}/voting/start")
    suspend fun startVotingSession(@Path("id") groupId: String): retrofit2.Response<StartVotingSessionResponse>

    @POST("/groups/{id}/voting/end")
    suspend fun endVotingSession(@Path("id") groupId: String): retrofit2.Response<VotingResultResponse>

    @GET("/groups/{id}/voting")
    suspend fun getVotingSession(@Path("id") groupId: String): retrofit2.Response<StartVotingSessionResponse>

    @POST("/groups/{id}/voting/vote")
    suspend fun submitVote(
        @Path("id") groupId: String,
        @Body request: SubmitVoteRequest
    ): retrofit2.Response<Unit>

    @GET("/groups/{id}/voting/result")
    suspend fun getVotingResult(@Path("id") groupId: String): retrofit2.Response<VotingResultResponse>

    @DELETE("/groups/{id}")
    suspend fun deleteGroup(@Path("id") groupId: String): retrofit2.Response<Unit>
}

interface UserApiService {
    @POST("/auth/google")
    suspend fun authenticateWithGoogle(@Body request: AuthRequest): Response<AuthResponse>

    @PUT("/users/me")
    suspend fun updateProfile(
        @Header("Authorization") bearerToken: String,
        @Body request: UpdateUserRequest
    ): Response<UpdateUserResponse>
}
