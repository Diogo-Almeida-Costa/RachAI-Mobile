package org.example.rachai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val isNameValid: Boolean = true,
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false
)

@Composable
fun RegisterScreen(
    state: RegisterUiState,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onNavigateToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        RachaiHeader(title = "Criar Conta")

        Spacer(modifier = Modifier.height(24.dp))

        RachaiTextField(
            value = state.name,
            onValueChange = onNameChange,
            label = "Nome completo",
            isError = !state.isNameValid,
            errorMessage = if (!state.isNameValid) "Nome obrigatório" else null
        )

        Spacer(modifier = Modifier.height(16.dp))

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
            errorMessage = if (!state.isPasswordValid) "Mínimo de 6 caracteres" else null
        )

        Spacer(modifier = Modifier.height(24.dp))

        RachaiButton(
            text = if (state.isLoading) "Cadastrando..." else "Cadastrar",
            onClick = onRegisterClick,
            enabled = state.isFormValid && !state.isLoading
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = onNavigateToLogin) {
            Text("Já possui conta? Faça Login")
        }
    }
}