package com.example.movieswipe.ui.group

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.movieswipe.domain.model.Group
import com.example.movieswipe.domain.usecase.CreateGroupUseCase
import com.example.movieswipe.domain.usecase.JoinGroupUseCase
import com.example.movieswipe.domain.usecase.GetGroupUseCase
import com.example.movieswipe.domain.usecase.SetPreferencesUseCase
import com.example.movieswipe.domain.usecase.DeleteGroupUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val createGroupUseCase: CreateGroupUseCase,
    private val joinGroupUseCase: JoinGroupUseCase,
    private val getGroupUseCase: GetGroupUseCase,
    private val setPreferencesUseCase: SetPreferencesUseCase,
    private val deleteGroupUseCase: DeleteGroupUseCase
) : ViewModel() {
    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun createGroup(name: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = createGroupUseCase(name)
            _loading.value = false
            result.onSuccess { _group.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun joinGroup(invitationCode: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = joinGroupUseCase(invitationCode)
            _loading.value = false
            result.onSuccess { _group.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun getGroup(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = getGroupUseCase(groupId)
            _loading.value = false
            result.onSuccess { _group.value = it }
                .onFailure { _error.value = it.message }
        }
    }

    fun setPreferences(groupId: String, genres: List<Int>) {
        viewModelScope.launch {
            _loading.value = true
            val result = setPreferencesUseCase(groupId, genres)
            _loading.value = false
            result.onFailure { _error.value = it.message }
        }
    }

    fun deleteGroup(groupId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = deleteGroupUseCase(groupId)
            _loading.value = false
            result.onFailure { _error.value = it.message }
            if (result.isSuccess) _group.value = null
        }
    }
}

