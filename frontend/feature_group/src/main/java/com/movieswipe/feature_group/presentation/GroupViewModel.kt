package com.movieswipe.feature_group.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.movieswipe.feature_group.data.model.GroupDTO
import com.movieswipe.feature_group.domain.usecase.CreateGroupUseCase
import com.movieswipe.feature_group.domain.usecase.DeleteGroupUseCase
import com.movieswipe.feature_group.domain.usecase.GetGroupsUseCase
import com.movieswipe.feature_group.domain.usecase.JoinGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

sealed class GroupUiState {
    object Idle : GroupUiState()
    object Loading : GroupUiState()
    data class Success(val groups: List<GroupDTO>) : GroupUiState()
    data class Error(val message: String) : GroupUiState()
}

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val getGroupsUseCase: GetGroupsUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<GroupUiState>(GroupUiState.Idle)
    val uiState: StateFlow<GroupUiState> = _uiState

    fun loadGroups() {
        _uiState.value = GroupUiState.Loading
        viewModelScope.launch {
            val response = getGroupsUseCase()
            if (response.isSuccessful && response.body() != null) {
                _uiState.value = GroupUiState.Success(response.body()!!.groups)
            } else {
                _uiState.value = GroupUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun createGroup(name: String) {
        _uiState.value = GroupUiState.Loading
        viewModelScope.launch {
            val response = createGroupUseCase(name)
            if (response.isSuccessful && response.body() != null) {
                loadGroups()
            } else {
                _uiState.value = GroupUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun joinGroup(invitationCode: String) {
        _uiState.value = GroupUiState.Loading
        viewModelScope.launch {
            val response = joinGroupUseCase(invitationCode)
            if (response.isSuccessful && response.body() != null) {
                loadGroups()
            } else {
                _uiState.value = GroupUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }

    fun deleteGroup(groupId: String) {
        _uiState.value = GroupUiState.Loading
        viewModelScope.launch {
            val response = deleteGroupUseCase(groupId)
            if (response.isSuccessful) {
                loadGroups()
            } else {
                _uiState.value = GroupUiState.Error(response.errorBody()?.string() ?: "Unknown error")
            }
        }
    }
}

