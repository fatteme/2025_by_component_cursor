package com.movieswipe.feature_group.data.model

data class GroupDTO(
    val id: String,
    val name: String,
    val ownerId: String,
    val invitationCode: String,
    val members: List<String>,
    val isActive: Boolean,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateGroupRequestDTO(
    val name: String
)

data class JoinGroupRequestDTO(
    val invitationCode: String
)

data class GroupResponseDTO(
    val group: GroupDTO
)

data class GroupsResponseDTO(
    val groups: List<GroupDTO>
)

data class MessageResponseDTO(
    val message: String
)

data class ErrorResponseDTO(
    val error: String
)

