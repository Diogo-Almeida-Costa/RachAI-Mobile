package org.example.rachai.ui.expense

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import org.example.rachai.network.ExpenseResponse
import org.example.rachai.theme.LocalWindowSizeClass
import org.example.rachai.theme.WindowSizeClass
import org.example.rachai.ui.components.RachaiErrorState
import org.example.rachai.ui.components.RachaiLoadingState
import org.example.rachai.ui.components.RachaiTopBar

@Composable
fun AddExpenseScreen(
    groupId: Long,
    onBack: () -> Unit,
    onCreated: (ExpenseResponse) -> Unit,
    viewModel: AddExpenseViewModel = viewModel(key = "add-expense-$groupId") { AddExpenseViewModel(groupId) }
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val windowSizeClass = LocalWindowSizeClass.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState) {
        val state = uiState
        if (state is AddExpenseUiState.Created) onCreated(state.expense)
    }

    val handleBack: () -> Unit = {
        val state = uiState
        if (state is AddExpenseUiState.Ready && state.step > 1) {
            viewModel.prevStep()
        } else {
            onBack()
        }
    }

    Scaffold(
        topBar = {
            RachaiTopBar(
                title = "Nova Despesa",
                onBack = handleBack
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        when (val state = uiState) {
            is AddExpenseUiState.LoadingMembers -> RachaiLoadingState(modifier = Modifier.padding(padding))
            is AddExpenseUiState.MembersLoadError -> RachaiErrorState(
                message = state.message,
                modifier = Modifier.padding(padding)
            )
            is AddExpenseUiState.Created -> RachaiLoadingState(modifier = Modifier.padding(padding))
            is AddExpenseUiState.Ready -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (windowSizeClass != WindowSizeClass.Compact) {
                                    Modifier.widthIn(max = 680.dp)
                                } else {
                                    Modifier.fillMaxWidth()
                                }
                            )
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Indicador de etapas (Detalhes / Rateio)
                        StepIndicator(currentStep = state.step)

                        if (state.step == 1) {
                            ExpenseStep1(
                                description = state.description,
                                onDescriptionChange = viewModel::updateDescription,
                                amountText = state.amountText,
                                onAmountChange = viewModel::updateAmount,
                                errorMessage = state.errorMessage,
                                onNext = viewModel::nextStep,
                                onScanReceipt = {
                                    scope.launch {
                                        snackbarHostState.showSnackbar("Leitor de nota fiscal em breve!")
                                    }
                                }
                            )
                        } else {
                            ExpenseStep2(
                                state = state,
                                onSplitModeChange = viewModel::setSplitMode,
                                onSelectPayer = viewModel::selectPayer,
                                onToggleParticipant = viewModel::toggleParticipant,
                                onCustomShareChange = viewModel::setCustomShare,
                                onBack = viewModel::prevStep,
                                onSave = viewModel::submit
                            )
                        }
                    }
                }
            }
        }
    }
}
