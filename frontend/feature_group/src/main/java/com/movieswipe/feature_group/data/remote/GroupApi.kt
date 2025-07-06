package com.movieswipe.feature_group.data.remote

import com.movieswipe.feature_group.data.model.CreateGroupRequestDTO
import com.movieswipe.feature_group.data.model.GroupResponseDTO
import com.movieswipe.feature_group.data.model.GroupsResponseDTO
import com.movieswipe.feature_group.data.model.JoinGroupRequestDTO
import com.movieswipe.feature_group.data.model.MessageResponseDTO
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface GroupApi {
    @POST("/groups")
    suspend fun createGroup(@Body request: CreateGroupRequestDTO): Response<GroupResponseDTO>

    @GET("/groups")
    suspend fun getGroups(): Response<GroupsResponseDTO>

    @POST("/groups/join")
    suspend fun joinGroup(@Body request: JoinGroupRequestDTO): Response<GroupResponseDTO>

    @GET("/groups/{id}")
    suspend fun getGroup(@Path("id") groupId: String): Response<GroupResponseDTO>

    @DELETE("/groups/{id}")
    suspend fun deleteGroup(@Path("id") groupId: String): Response<MessageResponseDTO>
}

