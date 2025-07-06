package com.example.movieswipe.data.repository

import com.example.movieswipe.data.remote.GroupApiService
import com.example.movieswipe.data.remote.CreateGroupRequest
import com.example.movieswipe.data.remote.JoinGroupRequest
import com.example.movieswipe.data.remote.SetPreferencesRequest
import com.example.movieswipe.data.remote.SubmitVoteRequest
import com.example.movieswipe.domain.model.Group
import com.example.movieswipe.domain.model.VotingSession
import com.example.movieswipe.domain.model.VotingResult
import com.example.movieswipe.domain.repository.GroupRepository
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val api: GroupApiService
) : GroupRepository {
    override suspend fun createGroup(name: String) = try {
        val response = api.createGroup(CreateGroupRequest(name))
        if (response.isSuccessful) Result.success(response.body()!!.group)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to create group"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun joinGroup(invitationCode: String) = try {
        val response = api.joinGroup(JoinGroupRequest(invitationCode))
        if (response.isSuccessful) Result.success(response.body()!!.group)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to join group"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getGroup(groupId: String) = try {
        val response = api.getGroup(groupId)
        if (response.isSuccessful) Result.success(response.body()!!.group)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get group"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun setPreferences(groupId: String, preferredGenres: List<Int>) = try {
        val response = api.setPreferences(groupId, SetPreferencesRequest(preferredGenres))
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to set preferences"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun startVotingSession(groupId: String) = try {
        val response = api.startVotingSession(groupId)
        if (response.isSuccessful) Result.success(response.body()!!.session)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to start voting session"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun endVotingSession(groupId: String) = try {
        val response = api.endVotingSession(groupId)
        if (response.isSuccessful) Result.success(response.body()!!.result)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to end voting session"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getVotingSession(groupId: String) = try {
        val response = api.getVotingSession(groupId)
        if (response.isSuccessful) Result.success(response.body()!!.session)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get voting session"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun submitVote(groupId: String, movieTmdbId: Int, vote: Boolean) = try {
        val response = api.submitVote(groupId, SubmitVoteRequest(movieTmdbId, vote))
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to submit vote"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun getVotingResult(groupId: String) = try {
        val response = api.getVotingResult(groupId)
        if (response.isSuccessful) Result.success(response.body()!!.result)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to get voting result"))
    } catch (e: Exception) { Result.failure(e) }

    override suspend fun deleteGroup(groupId: String) = try {
        val response = api.deleteGroup(groupId)
        if (response.isSuccessful) Result.success(Unit)
        else Result.failure(Exception(response.errorBody()?.string() ?: "Failed to delete group"))
    } catch (e: Exception) { Result.failure(e) }
}

