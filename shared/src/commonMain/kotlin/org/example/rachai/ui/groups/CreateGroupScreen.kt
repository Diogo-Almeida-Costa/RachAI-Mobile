package org.example.rachai.ui.groups

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
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.network.GroupResponse
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiTextField
import org.example.rachai.ui.components.RachaiTopBar

@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onCreated: (GroupResponse) -> Unit,
    viewModel: CreateGroupViewModel = viewModel { CreateGroupViewModel() }
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is CreateGroupUiState.Created) {
            onCreated(state.group)
        }
    }

    Scaffold(
        topBar = { RachaiTopBar(title = "Novo grupo", onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(RachaiSpacing.md),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.GroupAdd,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Criar novo grupo",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Dê um nome e, se quiser, uma descrição para o grupo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
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
                        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
                    ) {
                        RachaiTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nome do grupo",
                            isError = uiState is CreateGroupUiState.Error,
                            supportingText = if (name.isBlank()) "Ex.: Viagem à praia, Apartamento 302" else null
                        )

                        RachaiTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = "Descrição (opcional)",
                            supportingText = "Ex.: Divisão de contas e mercado"
                        )

                        if (uiState is CreateGroupUiState.Error) {
                            Text(
                                text = (uiState as CreateGroupUiState.Error).message,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        RachaiPrimaryButton(
                            text = "Criar grupo",
                            onClick = { viewModel.createGroup(name, description) },
                            loading = uiState is CreateGroupUiState.Saving,
                            modifier = Modifier.padding(top = RachaiSpacing.sm)
                        )
                    }
                }
            }
        }
    }
}
