package com.movieswipe.feature_movie.domain.usecase

import com.movieswipe.feature_movie.data.model.*
import com.movieswipe.feature_movie.data.repository.MovieRepository
import retrofit2.Response
import javax.inject.Inject

class GetGenresUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(): Response<GenresResponseDTO> = repository.getGenres()
}

class GetMovieUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(movieId: Int): Response<MovieResponseDTO> = repository.getMovie(movieId)
}

class GetRecommendationUseCase @Inject constructor(private val repository: MovieRepository) {
    suspend operator fun invoke(request: RecommendationRequestDTO): Response<RecommendationResponseDTO> = repository.getRecommendation(request)
}

