package org.example.rachai.ui.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.example.rachai.auth.SessionManager
import org.example.rachai.network.ApiResult
import org.example.rachai.network.ExpenseApi
import org.example.rachai.network.ExpenseRequest
import org.example.rachai.network.ExpenseResponse
import org.example.rachai.network.ExpenseSplitRequest
import org.example.rachai.network.GroupApi
import org.example.rachai.network.UserApi
import kotlin.math.abs
import kotlin.math.round

data class GroupMemberOption(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val isCurrentUser: Boolean = false
) {
    val displayName: String
        get() = if (isCurrentUser) "$firstName (Você)" else "$firstName $lastName".trim()
}

enum class SplitMode {
    Equal, Custom
}

sealed class AddExpenseUiState {
    data object LoadingMembers : AddExpenseUiState()
    data class Ready(
        val members: List<GroupMemberOption>,
        val selectedPayerId: Long? = null,
        val selectedParticipantIds: Set<Long> = emptySet(),
        val saving: Boolean = false,
        val errorMessage: String? = null,
        val step: Int = 1,
        val splitMode: SplitMode = SplitMode.Equal,
        val description: String = "",
        val amountText: String = "",
        val amount: Double = 0.0,
        val customShares: Map<Long, String> = emptyMap()
    ) : AddExpenseUiState() {
        val totalCustomShares: Double
            get() = selectedParticipantIds.sumOf { id ->
                customShares[id]?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
            }

        val isSumMatching: Boolean
            get() = when (splitMode) {
                SplitMode.Equal -> true
                SplitMode.Custom -> abs(totalCustomShares - amount) < 0.015
            }

        fun calculatedShareFor(memberId: Long): Double {
            if (memberId !in selectedParticipantIds) return 0.0
            return when (splitMode) {
                SplitMode.Equal -> {
                    val count = selectedParticipantIds.size
                    if (count == 0) 0.0 else round((amount / count) * 100.0) / 100.0
                }
                SplitMode.Custom -> {
                    customShares[memberId]?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
                }
            }
        }
    }
    data class MembersLoadError(val message: String) : AddExpenseUiState()
    data class Created(val expense: ExpenseResponse) : AddExpenseUiState()
}

class AddExpenseViewModel(
    private val groupId: Long,
    private val groupApi: GroupApi = GroupApi(),
    private val userApi: UserApi = UserApi(),
    private val expenseApi: ExpenseApi = ExpenseApi()
) : ViewModel() {

    private val _uiState = MutableStateFlow<AddExpenseUiState>(AddExpenseUiState.LoadingMembers)
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadMembers()
    }

    private fun loadMembers() {
        _uiState.value = AddExpenseUiState.LoadingMembers
        viewModelScope.launch {
            val groupResult = groupApi.getGroupById(groupId)
            val allUsersResult = userApi.listAllUsers()

            if (groupResult is ApiResult.Error) {
                _uiState.value = AddExpenseUiState.MembersLoadError(groupResult.message)
                return@launch
            }
            if (allUsersResult is ApiResult.Error) {
                _uiState.value = AddExpenseUiState.MembersLoadError(allUsersResult.message)
                return@launch
            }

            val group = (groupResult as ApiResult.Success).data
            val allUsers = (allUsersResult as ApiResult.Success).data
            val usersByEmail = allUsers.associateBy { it.email }
            val currentEmail = SessionManager.currentUser?.email

            val members = group.members.mapNotNull { profile ->
                usersByEmail[profile.email]?.let { user ->
                    GroupMemberOption(
                        id = user.id,
                        firstName = profile.firstName,
                        lastName = profile.lastName,
                        email = profile.email,
                        isCurrentUser = profile.email.equals(currentEmail, ignoreCase = true)
                    )
                }
            }

            if (members.isEmpty()) {
                _uiState.value = AddExpenseUiState.MembersLoadError("Não foi possível identificar os membros do grupo")
                return@launch
            }

            val defaultPayer = members.firstOrNull { it.isCurrentUser } ?: members.first()

            _uiState.value = AddExpenseUiState.Ready(
                members = members,
                selectedPayerId = defaultPayer.id,
                selectedParticipantIds = members.map { it.id }.toSet()
            )
        }
    }

    fun updateDescription(newDescription: String) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        _uiState.value = state.copy(description = newDescription, errorMessage = null)
    }

    fun updateAmount(newAmountText: String) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        val sanitized = newAmountText.filterIndexed { index, c ->
            c.isDigit() || ((c == '.' || c == ',') && newAmountText.indexOfFirst { it == '.' || it == ',' } == index)
        }
        val parsed = sanitized.replace(',', '.').toDoubleOrNull() ?: 0.0
        _uiState.value = state.copy(amountText = sanitized, amount = parsed, errorMessage = null)
    }

    fun nextStep() {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        if (state.description.isBlank()) {
            _uiState.value = state.copy(errorMessage = "Informe a descrição da despesa")
            return
        }
        if (state.amount <= 0.0) {
            _uiState.value = state.copy(errorMessage = "Informe um valor total maior que zero")
            return
        }

        // Inicializa parcelas personalizadas caso vá para o modo personalizado depois
        val participantCount = state.selectedParticipantIds.size
        val equalPart = if (participantCount > 0) state.amount / participantCount else 0.0
        val formattedEqual = "%.2f".format(equalPart).replace('.', ',')
        val initialCustomShares = state.selectedParticipantIds.associateWith { formattedEqual }

        _uiState.value = state.copy(
            step = 2,
            errorMessage = null,
            customShares = if (state.customShares.isEmpty()) initialCustomShares else state.customShares
        )
    }

    fun prevStep() {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        _uiState.value = state.copy(step = 1, errorMessage = null)
    }

    fun setSplitMode(mode: SplitMode) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        val count = state.selectedParticipantIds.size
        val shares = if (mode == SplitMode.Custom && state.customShares.isEmpty() && count > 0) {
            val part = state.amount / count
            val partText = "%.2f".format(part).replace('.', ',')
            state.selectedParticipantIds.associateWith { partText }
        } else {
            state.customShares
        }
        _uiState.value = state.copy(splitMode = mode, customShares = shares, errorMessage = null)
    }

    fun setCustomShare(memberId: Long, shareText: String) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        val sanitized = shareText.filterIndexed { index, c ->
            c.isDigit() || ((c == '.' || c == ',') && shareText.indexOfFirst { it == '.' || it == ',' } == index)
        }
        val updated = state.customShares.toMutableMap()
        updated[memberId] = sanitized
        _uiState.value = state.copy(customShares = updated, errorMessage = null)
    }

    fun selectPayer(id: Long) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        _uiState.value = state.copy(selectedPayerId = id)
    }

    fun toggleParticipant(id: Long) {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        val updated = if (id in state.selectedParticipantIds) {
            state.selectedParticipantIds - id
        } else {
            state.selectedParticipantIds + id
        }
        // Atualiza custom shares se necessário
        val updatedCustom = state.customShares.toMutableMap()
        if (id in updated && id !in state.customShares) {
            val count = updated.size
            val part = if (count > 0) state.amount / count else 0.0
            updatedCustom[id] = "%.2f".format(part).replace('.', ',')
        }
        _uiState.value = state.copy(
            selectedParticipantIds = updated,
            customShares = updatedCustom,
            errorMessage = null
        )
    }

    fun submit() {
        val state = _uiState.value as? AddExpenseUiState.Ready ?: return
        val payerId = state.selectedPayerId

        if (state.description.isBlank()) {
            _uiState.value = state.copy(step = 1, errorMessage = "Descreva a despesa")
            return
        }
        if (state.amount <= 0.0) {
            _uiState.value = state.copy(step = 1, errorMessage = "Informe um valor total válido")
            return
        }
        if (payerId == null) {
            _uiState.value = state.copy(errorMessage = "Selecione quem pagou")
            return
        }
        if (state.selectedParticipantIds.isEmpty()) {
            _uiState.value = state.copy(errorMessage = "Selecione ao menos um participante")
            return
        }

        val participants = state.selectedParticipantIds.toList()

        val splits: List<ExpenseSplitRequest> = when (state.splitMode) {
            SplitMode.Equal -> {
                val equalShare = state.amount / participants.size
                participants.mapIndexed { index, debtorId ->
                    val share = if (index == participants.lastIndex) {
                        state.amount - (equalShare * (participants.size - 1))
                    } else {
                        equalShare
                    }
                    ExpenseSplitRequest(debtorId = debtorId, share = round(share * 100.0) / 100.0)
                }
            }
            SplitMode.Custom -> {
                if (!state.isSumMatching) {
                    val diff = state.totalCustomShares - state.amount
                    val diffFormatted = "%.2f".format(abs(diff))
                    val direction = if (diff > 0) "ultrapassa" else "está abaixo de"
                    _uiState.value = state.copy(
                        errorMessage = "A soma das parcelas $direction R$ $diffFormatted do valor total"
                    )
                    return
                }
                participants.map { debtorId ->
                    val share = state.customShares[debtorId]?.replace(',', '.')?.toDoubleOrNull() ?: 0.0
                    ExpenseSplitRequest(debtorId = debtorId, share = share)
                }
            }
        }

        _uiState.value = state.copy(saving = true, errorMessage = null)
        viewModelScope.launch {
            val request = ExpenseRequest(
                description = state.description.trim(),
                amount = state.amount,
                payerId = payerId,
                groupId = groupId,
                splits = splits
            )
            when (val result = expenseApi.createExpense(request)) {
                is ApiResult.Success -> _uiState.value = AddExpenseUiState.Created(result.data)
                is ApiResult.Error -> _uiState.value = state.copy(saving = false, errorMessage = result.message)
            }
        }
    }
}
