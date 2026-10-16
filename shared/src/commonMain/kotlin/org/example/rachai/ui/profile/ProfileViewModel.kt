package org.example.rachai.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.auth.SessionManager
import org.example.rachai.network.ApiResult
import org.example.rachai.network.UserApi
import org.example.rachai.network.UserProfile

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Loaded(val profile: UserProfile, val saving: Boolean = false, val savedJustNow: Boolean = false) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}

class ProfileViewModel(
    private val userApi: UserApi = UserApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            when (val result = userApi.getMyProfile()) {
                is ApiResult.Success -> {
                    SessionManager.updateProfile(result.data)
                    _uiState.value = ProfileUiState.Loaded(result.data)
                }
                is ApiResult.Error -> _uiState.value = ProfileUiState.Error(result.message)
            }
        }
    }

    fun saveBio(bio: String) {
        val state = _uiState.value as? ProfileUiState.Loaded ?: return
        _uiState.value = state.copy(saving = true)
        viewModelScope.launch {
            val updated = state.profile.copy(bio = bio)
            when (val result = userApi.updateMyProfile(updated)) {
                is ApiResult.Success -> {
                    SessionManager.updateProfile(result.data)
                    _uiState.value = ProfileUiState.Loaded(result.data, savedJustNow = true)
                }
                is ApiResult.Error -> _uiState.value = state.copy(saving = false)
            }
        }
    }

    fun logout() {
        SessionManager.clear()
    }
}
