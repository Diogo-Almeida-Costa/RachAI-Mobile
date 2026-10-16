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

sealed class GroupsUiState {
    data object Loading : GroupsUiState()
    data class Loaded(val groups: List<GroupResponse>) : GroupsUiState()
    data class Error(val message: String) : GroupsUiState()
}

class GroupsViewModel(
    private val groupApi: GroupApi = GroupApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupsUiState>(GroupsUiState.Loading)
    val uiState: StateFlow<GroupsUiState> = _uiState.asStateFlow()

    init {
        load(silent = false)
    }

    fun load(silent: Boolean = false) {
        val wasLoaded = _uiState.value is GroupsUiState.Loaded
        if (!wasLoaded) {
            _uiState.value = GroupsUiState.Loading
        }
        viewModelScope.launch {
            when (val result = groupApi.getMyGroups()) {
                is ApiResult.Success -> {
                    _uiState.value = GroupsUiState.Loaded(result.data)
                }
                is ApiResult.Error -> {
                    // Se não tínhamos dados carregados antes (ou se não for chamada silenciosa),
                    // NUNCA manter a tela travada no estado de Loading:
                    if (!wasLoaded || !silent) {
                        _uiState.value = GroupsUiState.Error(result.message)
                    }
                }
            }
        }
    }
}
