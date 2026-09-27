package org.example.rachai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.rachai.ui.components.RachaiButton
import com.rachai.ui.components.RachaiHeader
import com.rachai.ui.components.RachaiTextField

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

@Composable
fun LoginScreen(
    state: LoginUiState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onNavigateToRegister: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RachaiHeader(title = "Bem-vindo ao RachAI")

        Spacer(modifier = Modifier.height(24.dp))

        RachaiTextField(
            value = state.email,
            onValueChange = onEmailChange,
            label = "E-mail",
            isError = !state.isEmailValid,
            errorMessage = if (!state.isEmailValid) "E-mail inválido" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

        RachaiTextField(
            value = state.password,
            onValueChange = onPasswordChange,
            label = "Senha",
            visualTransformation = PasswordVisualTransformation(),
            isError = !state.isPasswordValid,
            errorMessage = if (!state.isPasswordValid) "A senha deve ter no mínimo 6 caracteres" else null
        )

        if (state.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        RachaiButton(
            text = if (state.isLoading) "Entrando..." else "Entrar",
            onClick = onLoginClick,
            enabled = state.isFormValid && !state.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToRegister) {
            Text("Ainda não tem conta? Cadastre-se")
        }
    }
}