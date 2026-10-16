package org.example.rachai.ui.groups

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.auth.SessionManager
import org.example.rachai.network.GroupResponse
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.theme.WindowSizeClass
import org.example.rachai.ui.components.GroupCard
import org.example.rachai.ui.components.RachaiAvatar
import org.example.rachai.ui.components.RachaiEmptyState
import org.example.rachai.ui.components.RachaiErrorState
import org.example.rachai.ui.components.RachaiLoadingState
import org.example.rachai.ui.components.RachaiTopBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupsScreen(
    onOpenGroup: (GroupResponse) -> Unit,
    onCreateGroup: () -> Unit,
    onOpenProfile: () -> Unit,
    viewModel: GroupsViewModel = viewModel { GroupsViewModel() }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val user = SessionManager.currentUser
    val windowSizeClass = LocalWindowSizeClass.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.load(silent = true) }

    Scaffold(
        topBar = {
            RachaiTopBar(
                title = "Seus grupos",
                trailing = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { viewModel.load(silent = false) }) {
                            Icon(
                                Icons.Filled.Refresh,
                                contentDescription = "Atualizar lista de grupos",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        RachaiAvatar(
                            firstName = user?.firstName ?: "",
                            lastName = user?.lastName ?: "",
                            size = 36.dp,
                            modifier = Modifier
                                .clickable(onClick = onOpenProfile)
                                .padding(end = RachaiSpacing.md)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onCreateGroup,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Criar grupo")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter
        ) {
            when (val state = uiState) {
                is GroupsUiState.Loading -> RachaiLoadingState()
                is GroupsUiState.Error -> RachaiErrorState(
                    message = state.message,
                    onRetry = { viewModel.load(silent = false) }
                )
                is GroupsUiState.Loaded -> {
                    if (state.groups.isEmpty()) {
                        RachaiEmptyState(
                            title = "Nenhum grupo ainda",
                            description = "Crie um grupo para começar a dividir despesas com seus amigos.",
                            actionLabel = "Criar grupo",
                            onAction = onCreateGroup
                        )
                    } else {
                        // Layout adaptativo: 1 coluna em celular, 2 colunas em telas médias/largas (tablets)
                        val columns = if (windowSizeClass == WindowSizeClass.Compact) 1 else 2
                        val horizontalPadding = if (windowSizeClass == WindowSizeClass.Expanded) 32.dp else RachaiSpacing.md

                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            contentPadding = PaddingValues(
                                horizontal = horizontalPadding,
                                vertical = RachaiSpacing.md
                            ),
                            horizontalArrangement = Arrangement.spacedBy(RachaiSpacing.md),
                            verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm),
                            modifier = Modifier
                                .fillMaxSize()
                                .then(
                                    if (windowSizeClass != WindowSizeClass.Compact) {
                                        Modifier.widthIn(max = 840.dp)
                                    } else Modifier
                                )
                        ) {
                            items(state.groups, key = { it.id }) { group ->
                                GroupCard(
                                    name = group.name,
                                    description = group.description.orEmpty(),
                                    memberCount = group.members.size,
                                    onClick = { onOpenGroup(group) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
