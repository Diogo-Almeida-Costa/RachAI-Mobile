package org.example.rachai.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Payments
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
import org.example.rachai.theme.WindowSizeClass
import org.example.rachai.ui.components.RachaiPasswordField
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiTextField
import org.example.rachai.ui.components.RachaiTextLink

@Composable
fun LoginScreen(
    onLoginSuccess: (email: String) -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel { AuthViewModel() }
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val windowSizeClass = LocalWindowSizeClass.current

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        if (uiState is AuthUiState.LoggedIn) {
            onLoginSuccess(email.trim())
        }
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
                .widthIn(max = 440.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = RachaiSpacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Ícone do App
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.Payments,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(RachaiSpacing.md))

            Text(
                text = "Rachai",
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Bem-vindo de volta",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(top = RachaiSpacing.xs)
            )

            Text(
                text = "Entre para continuar dividindo despesas com seus amigos",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = RachaiSpacing.xs, bottom = RachaiSpacing.lg)
            )

            // Card do Formulário
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
                    verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
                ) {
                    RachaiTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = "E-mail",
                        keyboardType = KeyboardType.Email,
                        isError = uiState is AuthUiState.Error
                    )

                    RachaiPasswordField(
                        value = password,
                        onValueChange = { password = it },
                        label = "Senha",
                        isError = uiState is AuthUiState.Error
                    )

                    if (uiState is AuthUiState.Error) {
                        Text(
                            text = (uiState as AuthUiState.Error).message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    RachaiPrimaryButton(
                        text = "Entrar",
                        onClick = { viewModel.login(email, password) },
                        loading = uiState is AuthUiState.Loading,
                        modifier = Modifier.padding(top = RachaiSpacing.xs)
                    )
                }
            }

            RachaiTextLink(
                text = "Não tem conta? Cadastre-se",
                onClick = onNavigateToRegister,
                modifier = Modifier.padding(top = RachaiSpacing.md)
            )
        }
    }
}
