package com.movieswipe.feature_group.data.repository

import com.movieswipe.feature_group.data.model.CreateGroupRequestDTO
import com.movieswipe.feature_group.data.model.GroupResponseDTO
import com.movieswipe.feature_group.data.model.GroupsResponseDTO
import com.movieswipe.feature_group.data.model.JoinGroupRequestDTO
import com.movieswipe.feature_group.data.model.MessageResponseDTO
import com.movieswipe.feature_group.data.remote.GroupApi
import retrofit2.Response
import javax.inject.Inject

class GroupRepository @Inject constructor(
    private val api: GroupApi
) {
    suspend fun createGroup(name: String): Response<GroupResponseDTO> =
        api.createGroup(CreateGroupRequestDTO(name))

    suspend fun getGroups(): Response<GroupsResponseDTO> =
        api.getGroups()

    suspend fun joinGroup(invitationCode: String): Response<GroupResponseDTO> =
        api.joinGroup(JoinGroupRequestDTO(invitationCode))

    suspend fun getGroup(groupId: String): Response<GroupResponseDTO> =
        api.getGroup(groupId)

    suspend fun deleteGroup(groupId: String): Response<MessageResponseDTO> =
        api.deleteGroup(groupId)
}

