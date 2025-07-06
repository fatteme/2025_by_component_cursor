package com.movieswipe.feature_movie.data.model

data class GenreDTO(
    val _id: String?,
    val tmdbId: Int,
    val name: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class GenresResponseDTO(
    val genres: List<GenreDTO>
)

data class MovieGenreDTO(
    val id: Int,
    val name: String
)

data class MovieDTO(
    val _id: String?,
    val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val posterUrl: String?,
    val releaseDate: String,
    val genres: List<MovieGenreDTO>,
    val rating: Float,
    val createdAt: String?,
    val updatedAt: String?
)

data class MovieResponseDTO(
    val movie: MovieDTO
)

data class UserPreferenceDTO(
    val userId: String,
    val preferredGenres: List<Int>
)

data class RecommendationRequestDTO(
    val userPreferences: List<UserPreferenceDTO>
)

data class MovieRecommendationDTO(
    val movie: MovieDTO,
    val score: Float,
    val matchedGenres: List<String>
)

data class RecommendationResponseDTO(
    val recommendation: MovieRecommendationDTO
)

