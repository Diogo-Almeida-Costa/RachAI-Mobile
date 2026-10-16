package org.example.rachai.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.theme.WindowSizeClass
import org.example.rachai.ui.components.RachaiAvatar
import org.example.rachai.ui.components.RachaiErrorState
import org.example.rachai.ui.components.RachaiLoadingState
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiSecondaryButton
import org.example.rachai.ui.components.RachaiTextField
import org.example.rachai.ui.components.RachaiTopBar

@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onLoggedOut: () -> Unit,
    viewModel: ProfileViewModel = viewModel { ProfileViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var bio by remember { mutableStateOf("") }
    var bioTouched by remember { mutableStateOf(false) }
    val windowSizeClass = LocalWindowSizeClass.current

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is ProfileUiState.Loaded && !bioTouched) {
            bio = state.profile.bio.orEmpty()
        }
    }

    Scaffold(
        topBar = { RachaiTopBar(title = "Meu Perfil", onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is ProfileUiState.Loading -> RachaiLoadingState()
                is ProfileUiState.Error -> RachaiErrorState(
                    message = state.message,
                    onRetry = viewModel::load
                )
                is ProfileUiState.Loaded -> {
                    Column(
                        modifier = Modifier
                            .widthIn(max = 600.dp)
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(RachaiSpacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
                    ) {
                        // Card do Usuário (Avatar + Identificação)
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
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                RachaiAvatar(
                                    firstName = state.profile.firstName,
                                    lastName = state.profile.lastName,
                                    size = 80.dp
                                )
                                Spacer(modifier = Modifier.height(RachaiSpacing.sm))
                                Text(
                                    text = "${state.profile.firstName} ${state.profile.lastName}".trim(),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = state.profile.email,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Card de Edição da Bio
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
                                Text(
                                    text = "Sobre você",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                RachaiTextField(
                                    value = bio,
                                    onValueChange = { bio = it; bioTouched = true },
                                    label = "Bio / Recado",
                                    supportingText = "Ex.: Pagamentos via Pix chave CPF",
                                    singleLine = false
                                )

                                if (state.savedJustNow) {
                                    Text(
                                        text = "✓ Perfil atualizado com sucesso!",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(top = RachaiSpacing.xs)
                                    )
                                }

                                RachaiPrimaryButton(
                                    text = "Salvar alterações",
                                    onClick = { viewModel.saveBio(bio) },
                                    loading = state.saving,
                                    modifier = Modifier.padding(top = RachaiSpacing.xs)
                                )
                            }
                        }

                        // Botão Sair da conta
                        RachaiSecondaryButton(
                            text = "Sair da conta",
                            onClick = {
                                viewModel.logout()
                                onLoggedOut()
                            },
                            modifier = Modifier.padding(top = RachaiSpacing.xs, bottom = RachaiSpacing.lg)
                        )
                    }
                }
            }
        }
    }
}
