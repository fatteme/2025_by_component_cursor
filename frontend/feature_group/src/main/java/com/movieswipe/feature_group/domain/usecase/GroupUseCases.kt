package com.movieswipe.feature_group.domain.usecase

import com.movieswipe.feature_group.data.model.GroupResponseDTO
import com.movieswipe.feature_group.data.model.GroupsResponseDTO
import com.movieswipe.feature_group.data.model.MessageResponseDTO
import com.movieswipe.feature_group.data.repository.GroupRepository
import retrofit2.Response
import javax.inject.Inject

class CreateGroupUseCase @Inject constructor(private val repository: GroupRepository) {
    suspend operator fun invoke(name: String): Response<GroupResponseDTO> = repository.createGroup(name)
}

class GetGroupsUseCase @Inject constructor(private val repository: GroupRepository) {
    suspend operator fun invoke(): Response<GroupsResponseDTO> = repository.getGroups()
}

class JoinGroupUseCase @Inject constructor(private val repository: GroupRepository) {
    suspend operator fun invoke(invitationCode: String): Response<GroupResponseDTO> = repository.joinGroup(invitationCode)
}

class GetGroupUseCase @Inject constructor(private val repository: GroupRepository) {
    suspend operator fun invoke(groupId: String): Response<GroupResponseDTO> = repository.getGroup(groupId)
}

class DeleteGroupUseCase @Inject constructor(private val repository: GroupRepository) {
    suspend operator fun invoke(groupId: String): Response<MessageResponseDTO> = repository.deleteGroup(groupId)
}

