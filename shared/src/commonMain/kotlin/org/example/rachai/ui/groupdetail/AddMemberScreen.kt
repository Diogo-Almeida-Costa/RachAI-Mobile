package org.example.rachai.ui.groupdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.theme.WindowSizeClass
import org.example.rachai.ui.components.MemberRow
import org.example.rachai.ui.components.RachaiCard
import org.example.rachai.ui.components.RachaiEmptyState
import org.example.rachai.ui.components.RachaiErrorState
import org.example.rachai.ui.components.RachaiLoadingState
import org.example.rachai.ui.components.RachaiTextLink
import org.example.rachai.ui.components.RachaiTopBar

@Composable
fun AddMemberScreen(
    groupId: Long,
    onBack: () -> Unit,
    viewModel: AddMemberViewModel = viewModel(key = "add-member-$groupId") { AddMemberViewModel(groupId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSizeClass = LocalWindowSizeClass.current

    Scaffold(
        topBar = { RachaiTopBar(title = "Adicionar membro", onBack = onBack) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is AddMemberUiState.Loading -> RachaiLoadingState()
                is AddMemberUiState.Error -> RachaiErrorState(
                    message = state.message,
                    onRetry = viewModel::load
                )
                is AddMemberUiState.Loaded -> {
                    if (state.candidates.isEmpty()) {
                        RachaiEmptyState(
                            title = "Ninguém para adicionar",
                            description = "Todos os usuários cadastrados já fazem parte deste grupo."
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = if (windowSizeClass == WindowSizeClass.Expanded) 32.dp else RachaiSpacing.md,
                                vertical = RachaiSpacing.md
                            ),
                            verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm),
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (windowSizeClass != WindowSizeClass.Compact) {
                                        Modifier.widthIn(max = 680.dp)
                                    } else Modifier
                                )
                        ) {
                            item {
                                Text(
                                    text = "Selecione um usuário para convidar para o grupo:",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(bottom = RachaiSpacing.xs)
                                )
                            }
                            items(state.candidates, key = { it.id }) { user ->
                                RachaiCard {
                                    MemberRow(
                                        firstName = user.firstName,
                                        lastName = user.lastName,
                                        subtitle = user.email,
                                        trailing = {
                                            if (state.addingId == user.id) {
                                                CircularProgressIndicator(
                                                    modifier = Modifier.size(24.dp),
                                                    strokeWidth = 2.5.dp,
                                                    color = MaterialTheme.colorScheme.primary
                                                )
                                            } else {
                                                RachaiTextLink(
                                                    text = "Adicionar",
                                                    onClick = { viewModel.addMember(user.id) {} }
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
