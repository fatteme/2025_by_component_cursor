package com.example.movieswipe.domain.model

// Data models for MovieSwipe (User, Group, Movie, etc.)
data class User(
    val id: String,
    val googleId: String,
    val name: String,
    val email: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class Group(
    val id: String,
    val name: String,
    val ownerId: String
    // ...other fields
)

data class Genre(
    val id: String?, // _id from backend
    val tmdbId: Int,
    val name: String,
    val createdAt: String?,
    val updatedAt: String?
)

data class MovieGenre(
    val id: Int,
    val name: String
)

data class Movie(
    val id: String?, // _id from backend
    val tmdbId: Int,
    val title: String,
    val overview: String,
    val posterPath: String,
    val posterUrl: String?,
    val releaseDate: String,
    val genres: List<MovieGenre>,
    val rating: Float,
    val createdAt: String?,
    val updatedAt: String?
)

data class UserPreference(
    val userId: String,
    val preferredGenres: List<Int>
)

data class MovieRecommendation(
    val movie: Movie,
    val score: Float,
    val matchedGenres: List<String>
)
