package org.example.rachai.ui.previews

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.rachai.ui.screens.ExpenseUiModel
import com.rachai.ui.screens.HomeScreen
import com.rachai.ui.screens.HomeUiState
import com.rachai.ui.screens.LoginScreen
import com.rachai.ui.screens.LoginUiState
import com.rachai.ui.screens.RegisterScreen
import com.rachai.ui.screens.RegisterUiState

@Composable
fun LoginScreenPreviewValid() {
    MaterialTheme {
        LoginScreen(
            state = LoginUiState(
                email = "usuario@ufrn.br",
                password = "123456Password",
                isFormValid = true
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateToRegister = {}
        )
    }
}

@Composable
fun LoginScreenPreviewInvalid() {
    MaterialTheme {
        LoginScreen(
            state = LoginUiState(
                email = "email_invalido",
                password = "123",
                isEmailValid = false,
                isPasswordValid = false,
                isFormValid = false
            ),
            onEmailChange = {},
            onPasswordChange = {},
            onLoginClick = {},
            onNavigateToRegister = {}
        )
    }
}

@Composable
fun RegisterScreenPreview() {
    MaterialTheme {
        RegisterScreen(
            state = RegisterUiState(
                name = "José Diogo",
                email = "jose.diogo@ufrn.br",
                password = "Password123",
                isFormValid = true
            ),
            onNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onRegisterClick = {},
            onNavigateToLogin = {}
        )
    }
}

@Composable
fun HomeScreenWithDataPreview() {
    MaterialTheme {
        HomeScreen(
            state = HomeUiState(
                userName = "Diogo",
                expenses = listOf(
                    ExpenseUiModel("1", "Almoço RU", "R$ 15,00", "Filipe"),
                    ExpenseUiModel("2", "Uber até UFRN", "R$ 22,50", "Diogo")
                )
            )
        )
    }
}

@Composable
fun HomeScreenEmptyPreview() {
    MaterialTheme {
        HomeScreen(
            state = HomeUiState(userName = "Diogo", expenses = emptyList())
        )
    }
}