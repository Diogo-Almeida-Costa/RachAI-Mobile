package org.example.rachai.ui.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.network.ApiResult
import org.example.rachai.network.GroupApi
import org.example.rachai.network.GroupResponse

sealed class CreateGroupUiState {
    data object Idle : CreateGroupUiState()
    data object Saving : CreateGroupUiState()
    data class Created(val group: GroupResponse) : CreateGroupUiState()
    data class Error(val message: String) : CreateGroupUiState()
}

class CreateGroupViewModel(
    private val groupApi: GroupApi = GroupApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateGroupUiState>(CreateGroupUiState.Idle)
    val uiState: StateFlow<CreateGroupUiState> = _uiState.asStateFlow()

    fun createGroup(name: String, description: String) {
        if (name.isBlank()) {
            _uiState.value = CreateGroupUiState.Error("Dê um nome para o grupo")
            return
        }
        _uiState.value = CreateGroupUiState.Saving
        viewModelScope.launch {
            when (val result = groupApi.createGroup(name.trim(), description.trim().ifBlank { null })) {
                is ApiResult.Success -> _uiState.value = CreateGroupUiState.Created(result.data)
                is ApiResult.Error -> _uiState.value = CreateGroupUiState.Error(result.message)
            }
        }
    }
}
