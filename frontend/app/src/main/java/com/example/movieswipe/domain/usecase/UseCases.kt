package com.example.movieswipe.domain.usecase

import com.example.movieswipe.domain.model.Genre
import com.example.movieswipe.domain.model.Movie
import com.example.movieswipe.domain.model.MovieRecommendation
import com.example.movieswipe.domain.model.UserPreference
import com.example.movieswipe.domain.repository.MovieRepository

// Use cases for business logic (e.g., create group, join group, vote, etc.)
class CreateGroupUseCase {
    // ...to be implemented
}

class JoinGroupUseCase {
    // ...to be implemented
}

class StartVotingSessionUseCase {
    // ...to be implemented
}

class VoteForMovieUseCase {
    // ...to be implemented
}

class SelectMovieUseCase {
    // ...to be implemented
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
