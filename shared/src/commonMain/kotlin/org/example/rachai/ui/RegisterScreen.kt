package org.example.rachai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.auth.AuthUiState
import org.example.rachai.auth.AuthViewModel
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.ui.components.RachaiPasswordField
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiTextField
import org.example.rachai.ui.components.RachaiTextLink

@Composable
fun RegisterScreen(
    prefillEmail: String? = null,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: AuthViewModel = viewModel { AuthViewModel() }
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(prefillEmail.orEmpty()) }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val hasError = uiState is AuthUiState.Error

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.Registered) onRegisterSuccess()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = RachaiSpacing.md),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = RachaiSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.PersonAdd,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(modifier = Modifier.height(RachaiSpacing.sm))

            Text(
                text = "Criar conta",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Leva menos de um minuto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = RachaiSpacing.xs, bottom = RachaiSpacing.lg)
            )

            Card(
                shape = RoundedCornerShape(RachaiRadius.card),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(RachaiSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm)
                ) {
                    RachaiTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = "Nome"
                    )
                    RachaiTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = "Sobrenome"
                    )
                    RachaiTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        keyboardType = KeyboardType.Email
                    )
                    RachaiPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha (mín. 6 caracteres)"
                    )
                    RachaiPasswordField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = "Confirmar senha",
                        isError = hasError
                    )

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = RachaiSpacing.xs)
                        )
                    }

                    RachaiPrimaryButton(
                        text = "Cadastrar",
                        onClick = { viewModel.register(email, password, confirmPassword, firstName, lastName) },
                        loading = uiState is AuthUiState.Loading,
                        modifier = Modifier.padding(top = RachaiSpacing.sm)
                    )
                }
            }

            RachaiTextLink(
                text = "Já tem conta? Entrar",
                onClick = onNavigateToLogin,
                modifier = Modifier.padding(top = RachaiSpacing.md)
            )
        }
    }
}
