package org.example.rachai.ui.groupdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import org.example.rachai.network.ExpenseResponse
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.theme.WindowSizeClass
import androidx.compose.ui.text.font.FontWeight
import org.example.rachai.ui.components.BalanceRow
import org.example.rachai.ui.components.ExpenseRow
import org.example.rachai.ui.components.MemberRow
import org.example.rachai.ui.components.RachaiCard
import org.example.rachai.ui.components.RachaiEmptyState
import org.example.rachai.ui.components.RachaiErrorState
import org.example.rachai.ui.components.RachaiLoadingState
import org.example.rachai.ui.components.RachaiTopBar
import org.example.rachai.ui.components.formatCurrency
import org.example.rachai.ui.components.formatShortDate

private val tabs = listOf("Despesas", "Saldos", "Membros")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailScreen(
    groupId: Long,
    groupName: String,
    onBack: () -> Unit,
    onAddExpense: (Long) -> Unit,
    onOpenExpense: (ExpenseResponse) -> Unit,
    onAddMember: (Long) -> Unit,
    viewModel: GroupDetailViewModel = viewModel(key = "group-$groupId") { GroupDetailViewModel(groupId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val windowSizeClass = LocalWindowSizeClass.current

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) { viewModel.load(silent = true) }

    Scaffold(
        topBar = { RachaiTopBar(title = groupName, onBack = onBack) },
        floatingActionButton = {
            if (selectedTab == 0) {
                FloatingActionButton(onClick = { onAddExpense(groupId) }) {
                    Icon(Icons.Filled.Add, contentDescription = "Adicionar despesa")
                }
            } else if (selectedTab == 2) {
                FloatingActionButton(onClick = { onAddMember(groupId) }) {
                    Icon(Icons.Filled.PersonAdd, contentDescription = "Adicionar membro")
                }
            }
        }
    ) { padding ->
        when (val state = uiState) {
            is GroupDetailUiState.Loading -> RachaiLoadingState(modifier = Modifier.padding(padding))
            is GroupDetailUiState.Error -> RachaiErrorState(
                message = state.message,
                onRetry = { viewModel.load() },
                modifier = Modifier.padding(padding)
            )
            is GroupDetailUiState.Loaded -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                    PrimaryTabRow(selectedTabIndex = selectedTab) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = { Text(title) }
                            )
                        }
                    }

                    val horizontalPadding = if (windowSizeClass == WindowSizeClass.Expanded) 48.dp
                    else if (windowSizeClass == WindowSizeClass.Medium) 24.dp
                    else RachaiSpacing.md

                    when (selectedTab) {
                        0 -> ExpensesTab(state, onOpenExpense, horizontalPadding, windowSizeClass)
                        1 -> BalancesTab(state, onRecalculate = viewModel::recalculateDebts, onSettle = viewModel::settleDebt, horizontalPadding)
                        else -> MembersTab(state, horizontalPadding)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpensesTab(
    state: GroupDetailUiState.Loaded,
    onOpenExpense: (ExpenseResponse) -> Unit,
    horizontalPadding: androidx.compose.ui.unit.Dp,
    windowSizeClass: WindowSizeClass
) {
    if (state.expenses.isEmpty()) {
        RachaiEmptyState(
            title = "Nenhuma despesa ainda",
            description = "Toque no + para registrar a primeira despesa do grupo.",
            modifier = Modifier.fillMaxSize()
        )
        return
    }

    val columns = if (windowSizeClass == WindowSizeClass.Compact) 1 else 2

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = RachaiSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(RachaiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm),
        modifier = Modifier.fillMaxSize()
    ) {
        items(state.expenses, key = { it.id }) { expense ->
            ExpenseRow(
                description = expense.description,
                payerName = expense.payerName,
                amountLabel = formatCurrency(expense.amount),
                dateLabel = formatShortDate(expense.createdAt),
                onClick = { onOpenExpense(expense) }
            )
        }
    }
}

@Composable
private fun BalancesTab(
    state: GroupDetailUiState.Loaded,
    onRecalculate: () -> Unit,
    onSettle: (Long) -> Unit,
    horizontalPadding: androidx.compose.ui.unit.Dp
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = horizontalPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = RachaiSpacing.md),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Quem deve pra quem, já simplificado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = onRecalculate) {
                Icon(Icons.Filled.Sync, contentDescription = "Recalcular saldos")
            }
        }

        if (state.debts.isEmpty()) {
            RachaiEmptyState(
                title = "Tudo quitado",
                description = "Não há saldos pendentes neste grupo no momento.",
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = RachaiSpacing.sm),
                verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.debts, key = { it.id }) { debt ->
                    BalanceRow(
                        debtorName = debt.debtorName,
                        creditorName = debt.creditorName,
                        amountLabel = formatCurrency(debt.amount),
                        settled = debt.settled,
                        onSettle = { onSettle(debt.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun MembersTab(
    state: GroupDetailUiState.Loaded,
    horizontalPadding: androidx.compose.ui.unit.Dp
) {
    val ownerName = "${state.group.owner?.firstName.orEmpty()} ${state.group.owner?.lastName.orEmpty()}".trim()

    LazyColumn(
        contentPadding = PaddingValues(horizontal = horizontalPadding, vertical = RachaiSpacing.md),
        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.sm),
        modifier = Modifier.fillMaxSize()
    ) {
        if (ownerName.isNotBlank()) {
            item {
                androidx.compose.material3.Card(
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(org.example.rachai.theme.RachaiRadius.field),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                    ),
                    modifier = Modifier.fillMaxWidth().padding(bottom = RachaiSpacing.xs)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = RachaiSpacing.md, vertical = RachaiSpacing.sm),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "👑 Administrador: $ownerName",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
        items(state.group.members.toList()) { member ->
            RachaiCard {
                MemberRow(firstName = member.firstName, lastName = member.lastName, subtitle = member.email)
            }
        }
    }
}
