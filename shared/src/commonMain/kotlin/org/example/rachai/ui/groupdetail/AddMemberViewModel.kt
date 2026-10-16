package org.example.rachai.ui.groupdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.network.ApiResult
import org.example.rachai.network.GroupApi
import org.example.rachai.network.UserApi
import org.example.rachai.network.UserSummary

sealed class AddMemberUiState {
    data object Loading : AddMemberUiState()
    data class Loaded(val candidates: List<UserSummary>, val addingId: Long? = null) : AddMemberUiState()
    data class Error(val message: String) : AddMemberUiState()
}

class AddMemberViewModel(
    private val groupId: Long,
    private val groupApi: GroupApi = GroupApi(),
    private val userApi: UserApi = UserApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddMemberUiState>(AddMemberUiState.Loading)
    val uiState: StateFlow<AddMemberUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = AddMemberUiState.Loading
        viewModelScope.launch {
            val groupResult = groupApi.getGroupById(groupId)
            val usersResult = userApi.listAllUsers()

            if (groupResult is ApiResult.Error) {
                _uiState.value = AddMemberUiState.Error(groupResult.message)
                return@launch
            }
            if (usersResult is ApiResult.Error) {
                _uiState.value = AddMemberUiState.Error(usersResult.message)
                return@launch
            }

            val group = (groupResult as ApiResult.Success).data
            val users = (usersResult as ApiResult.Success).data
            val memberEmails = group.members.map { it.email }.toSet()

            val candidates = users.filterNot { it.email in memberEmails }
            _uiState.value = AddMemberUiState.Loaded(candidates)
        }
    }

    fun addMember(userId: Long, onAdded: () -> Unit) {
        val state = _uiState.value as? AddMemberUiState.Loaded ?: return
        _uiState.value = state.copy(addingId = userId)
        viewModelScope.launch {
            when (groupApi.addMember(groupId, userId)) {
                is ApiResult.Success -> {
                    onAdded()
                    load()
                }
                is ApiResult.Error -> {
                    val latest = _uiState.value as? AddMemberUiState.Loaded ?: return@launch
                    _uiState.value = latest.copy(addingId = null)
                }
            }
        }
    }
}
