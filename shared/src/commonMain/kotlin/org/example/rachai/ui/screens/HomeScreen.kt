package org.example.rachai.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rachai.ui.components.ExpenseCard
import com.rachai.ui.components.RachaiHeader

data class ExpenseUiModel(
    val id: String,
    val description: String,
    val amount: String,
    val payer: String
)

data class HomeUiState(
    val userName: String = "",
    val expenses: List<ExpenseUiModel> = emptyList(),
    val isLoading: Boolean = false
)

@Composable
fun HomeScreen(
    state: HomeUiState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        RachaiHeader(title = if (state.userName.isNotEmpty()) "Olá, ${state.userName}!" else "Suas Despesas")

        Spacer(modifier = Modifier.height(16.dp))

        if (state.expenses.isEmpty() && !state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Nenhuma despesa cadastrada ainda.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.expenses, key = { it.id }) { expense ->
                    ExpenseCard(
                        description = expense.description,
                        amount = expense.amount,
                        payer = expense.payer
                    )
                }
            }
        }
    }
}