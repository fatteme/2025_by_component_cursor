package com.example.movieswipe.domain.usecase

import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference
import com.example.movieswipe.domain.model.Group
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.VotingResult
import com.example.movieswipe.domain.repository.MovieRepository
import com.example.movieswipe.domain.repository.GroupRepository

// Use cases for business logic (e.g., create group, join group, vote, etc.)
class CreateGroupUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(name: String) = repo.createGroup(name)
}

class JoinGroupUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(invitationCode: String) = repo.joinGroup(invitationCode)
}

class GetGroupUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.getGroup(groupId)
}

class SetPreferencesUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String, genres: List<Int>) = repo.setPreferences(groupId, genres)
}

class StartVotingSessionUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.startVotingSession(groupId)
}

class EndVotingSessionUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.endVotingSession(groupId)
}

class GetVotingSessionUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.getVotingSession(groupId)
}

class SubmitVoteUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String, movieTmdbId: Int, vote: Boolean) = repo.submitVote(groupId, movieTmdbId, vote)
}

class GetVotingResultUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.getVotingResult(groupId)
}

class DeleteGroupUseCase(private val repo: GroupRepository) {
    suspend operator fun invoke(groupId: String) = repo.deleteGroup(groupId)
}

class GetGenresUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(): Result<List<Genre>> = repo.getGenres()
}

class GetMovieUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(tmdbId: Int): Result<Movie> = repo.getMovie(tmdbId)
}

class RecommendMovieUseCase(private val repo: MovieRepository) {
    suspend operator fun invoke(userPreferences: List<UserPreference>): Result<MovieRecommendation> =
        repo.recommendMovie(userPreferences)
}
