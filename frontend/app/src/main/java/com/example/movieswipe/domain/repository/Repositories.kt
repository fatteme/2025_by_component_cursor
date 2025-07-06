package com.example.movieswipe.domain.repository

import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.User
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference
import com.example.movieswipe.domain.model.Group
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.Vote
import com.example.movieswipe.domain.model.VotingResult

// Repository interfaces for abstracting data operations
interface GroupRepository {
    suspend fun createGroup(name: String): Result<Group>
    suspend fun joinGroup(invitationCode: String): Result<Group>
    suspend fun getGroup(groupId: String): Result<Group>
    suspend fun setPreferences(groupId: String, preferredGenres: List<Int>): Result<Unit>
    suspend fun startVotingSession(groupId: String): Result<VotingSession>
    suspend fun endVotingSession(groupId: String): Result<VotingResult>
    suspend fun getVotingSession(groupId: String): Result<VotingSession>
    suspend fun submitVote(groupId: String, movieTmdbId: Int, vote: Boolean): Result<Unit>
    suspend fun getVotingResult(groupId: String): Result<VotingResult>
    suspend fun deleteGroup(groupId: String): Result<Unit>
}

interface MovieRepository {
    suspend fun getGenres(): Result<List<Genre>>
    suspend fun getMovie(tmdbId: Int): Result<Movie>
    suspend fun recommendMovie(userPreferences: List<UserPreference>): Result<MovieRecommendation>
}

interface UserRepository {
    suspend fun authenticateWithGoogle(idToken: String): Result<Pair<String, User>>
    suspend fun updateProfile(token: String, name: String?, email: String?): Result<User>
    fun getCurrentUser(): User?
    fun getAuthToken(): String?
    fun logout()
}
