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

data class Movie(
    val id: String,
    val title: String,
    val genres: List<String>
    // ...other fields
)
