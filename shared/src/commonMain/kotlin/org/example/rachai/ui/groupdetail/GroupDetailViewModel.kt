package org.example.rachai.ui.groupdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.network.ApiResult
import org.example.rachai.network.DebtApi
import org.example.rachai.network.DebtResponse
import org.example.rachai.network.ExpenseApi
import org.example.rachai.network.ExpenseResponse
import org.example.rachai.network.GroupApi
import org.example.rachai.network.GroupResponse

sealed class GroupDetailUiState {
    data object Loading : GroupDetailUiState()
    data class Loaded(
        val group: GroupResponse,
        val expenses: List<ExpenseResponse>,
        val debts: List<DebtResponse>,
        val recalculatingDebts: Boolean = false
    ) : GroupDetailUiState()
    data class Error(val message: String) : GroupDetailUiState()
}

class GroupDetailViewModel(
    private val groupId: Long,
    private val groupApi: GroupApi = GroupApi(),
    private val expenseApi: ExpenseApi = ExpenseApi(),
    private val debtApi: DebtApi = DebtApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<GroupDetailUiState>(GroupDetailUiState.Loading)
    val uiState: StateFlow<GroupDetailUiState> = _uiState.asStateFlow()

    init {
        load()
    }

    fun load(silent: Boolean = false) {
        if (!silent || _uiState.value !is GroupDetailUiState.Loaded) {
            _uiState.value = GroupDetailUiState.Loading
        }
        viewModelScope.launch {
            val groupResult = groupApi.getGroupById(groupId)
            if (groupResult is ApiResult.Error) {
                if (!silent) _uiState.value = GroupDetailUiState.Error(groupResult.message)
                return@launch
            }
            val group = (groupResult as ApiResult.Success).data

            val expensesResult = expenseApi.getExpensesByGroup(groupId)
            val debtsResult = debtApi.getDebtsByGroup(groupId)

            val expenses = (expensesResult as? ApiResult.Success)?.data ?: emptyList()
            val debts = (debtsResult as? ApiResult.Success)?.data ?: emptyList()

            _uiState.value = GroupDetailUiState.Loaded(group = group, expenses = expenses, debts = debts)
        }
    }

    /** Recalcula os saldos simplificados a partir das despesas atuais do grupo. */
    fun recalculateDebts() {
        val current = _uiState.value as? GroupDetailUiState.Loaded ?: return
        _uiState.value = current.copy(recalculatingDebts = true)
        viewModelScope.launch {
            when (val result = debtApi.calculateDebts(groupId)) {
                is ApiResult.Success -> {
                    val latest = _uiState.value as? GroupDetailUiState.Loaded ?: return@launch
                    _uiState.value = latest.copy(debts = result.data, recalculatingDebts = false)
                }
                is ApiResult.Error -> {
                    val latest = _uiState.value as? GroupDetailUiState.Loaded ?: return@launch
                    _uiState.value = latest.copy(recalculatingDebts = false)
                }
            }
        }
    }

    fun settleDebt(debtId: Long) {
        val current = _uiState.value as? GroupDetailUiState.Loaded ?: return
        viewModelScope.launch {
            when (val result = debtApi.settleDebt(debtId)) {
                is ApiResult.Success -> {
                    val latest = _uiState.value as? GroupDetailUiState.Loaded ?: return@launch
                    _uiState.value = latest.copy(
                        debts = latest.debts.map { if (it.id == debtId) result.data else it }
                    )
                }
                is ApiResult.Error -> Unit
            }
        }
    }
}
