package org.example.rachai.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.network.ApiResult
import org.example.rachai.network.AuthApi

object SessionManager {
    var token: String? = null
        private set

    fun save(token: String) {
        this.token = token
    }

    fun clear() {
        token = null
    }

    val isLoggedIn: Boolean get() = token != null
}

sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data object LoggedIn : AuthUiState()
    data object Registered : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(
    private val authApi: AuthApi = AuthApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha e-mail e senha")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authApi.login(email.trim(), password)) {
                is ApiResult.Success -> {
                    SessionManager.save(result.data.token)
                    _uiState.value = AuthUiState.LoggedIn
                }
                is ApiResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    fun register(
        email: String,
        password: String,
        confirmPassword: String,
        firstName: String,
        lastName: String
    ) {
        if (firstName.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Preencha todos os campos obrigatórios")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("A senha deve conter pelo menos 6 caracteres")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("As senhas não coincidem")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            when (val result = authApi.register(
                email = email.trim(),
                password = password,
                firstName = firstName.trim(),
                lastName = lastName.trim()
            )) {
                is ApiResult.Success -> {
                    _uiState.value = AuthUiState.Registered
                }
                is ApiResult.Error -> {
                    _uiState.value = AuthUiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
