package org.example.rachai.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.rachai.theme.RachaiColors
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.ui.components.RachaiDropdownField
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiSecondaryButton
import org.example.rachai.ui.components.formatCurrency
import kotlin.math.abs

@Composable
fun ExpenseStep2(
    state: AddExpenseUiState.Ready,
    onSplitModeChange: (SplitMode) -> Unit,
    onSelectPayer: (Long) -> Unit,
    onToggleParticipant: (Long) -> Unit,
    onCustomShareChange: (Long, String) -> Unit,
    onBack: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedPayer = state.members.firstOrNull { it.id == state.selectedPayerId }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RachaiSpacing.md, vertical = RachaiSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
    ) {
        // Título "Dividir despesa"
        Text(
            text = "Dividir despesa",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        // Linha/Card com "Valor total"
        Card(
            shape = RoundedCornerShape(RachaiRadius.field),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = RachaiSpacing.md, vertical = RachaiSpacing.sm),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Valor total",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formatCurrency(state.amount),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Toggle: Igualitária / Personalizada
        SplitModeToggle(
            currentMode = state.splitMode,
            onModeSelect = onSplitModeChange
        )

        // Seção: Quem pagou
        Column(verticalArrangement = Arrangement.spacedBy(RachaiSpacing.xs)) {
            Text(
                text = "Quem pagou",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            RachaiDropdownField(
                label = "Pagador",
                options = state.members,
                selectedOption = selectedPayer,
                optionLabel = { it.displayName },
                onOptionSelected = { onSelectPayer(it.id) }
            )
        }

        // Seção: Participantes
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Participantes",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${state.selectedParticipantIds.size} selecionados",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Lista de Participantes
        Column(
            verticalArrangement = Arrangement.spacedBy(RachaiSpacing.xs),
            modifier = Modifier.fillMaxWidth()
        ) {
            state.members.forEach { member ->
                val isSelected = member.id in state.selectedParticipantIds
                val share = state.calculatedShareFor(member.id)
                val shareFormatted = formatCurrency(share)
                val customValue = state.customShares[member.id] ?: ""

                val rowA11y = "${member.displayName}: " +
                        (if (isSelected) "$shareFormatted, selecionado" else "não selecionado")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .sizeIn(minHeight = 48.dp)
                        .clip(RoundedCornerShape(RachaiRadius.field))
                        .background(MaterialTheme.colorScheme.surface)
                        .semantics(mergeDescendants = true) {
                            contentDescription = rowA11y
                        }
                        .padding(horizontal = RachaiSpacing.sm, vertical = RachaiSpacing.xs),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isSelected,
                        onCheckedChange = { onToggleParticipant(member.id) },
                        colors = CheckboxDefaults.colors(checkedColor = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.size(48.dp)
                    )

                    Text(
                        text = member.displayName,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onToggleParticipant(member.id) }
                    )

                    if (state.splitMode == SplitMode.Equal) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (isSelected) shareFormatted else "R$ 0,00",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        // Modo Personalizado: campo editável
                        OutlinedTextField(
                            value = if (isSelected) customValue else "0,00",
                            onValueChange = { if (isSelected) onCustomShareChange(member.id, it) },
                            enabled = isSelected,
                            singleLine = true,
                            prefix = { Text("R$ ", fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            shape = RoundedCornerShape(8.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier
                                .width(120.dp)
                                .height(52.dp)
                        )
                    }
                }
            }
        }

        // Card informativo do Rodapé com validação ao vivo
        val totalSplits = if (state.splitMode == SplitMode.Equal) state.amount else state.totalCustomShares
        val isMatching = state.isSumMatching

        Card(
            shape = RoundedCornerShape(RachaiRadius.field),
            colors = CardDefaults.cardColors(
                containerColor = if (isMatching) MaterialTheme.colorScheme.surfaceVariant
                else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .semantics {
                    liveRegion = LiveRegionMode.Polite
                    contentDescription = if (isMatching) {
                        "Soma das parcelas é igual ao total de ${formatCurrency(state.amount)}"
                    } else {
                        "Soma das parcelas difere do total de ${formatCurrency(state.amount)}"
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(RachaiSpacing.md),
                verticalArrangement = Arrangement.spacedBy(RachaiSpacing.xs)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Soma das parcelas / Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${formatCurrency(totalSplits)} / ${formatCurrency(state.amount)}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(RachaiSpacing.xs)
                ) {
                    if (isMatching) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = RachaiColors.Jade,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Soma igual ao valor total",
                            style = MaterialTheme.typography.bodySmall,
                            color = RachaiColors.Jade,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        val diff = abs(state.totalCustomShares - state.amount)
                        Icon(
                            Icons.Filled.Warning,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Diferença de ${formatCurrency(diff)} para o total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        if (state.errorMessage != null) {
            Text(
                text = state.errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = RachaiSpacing.xs)
            )
        }

        // Botões de Ação: VOLTAR e SALVAR DESPESA
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = RachaiSpacing.sm, bottom = RachaiSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
        ) {
            RachaiSecondaryButton(
                text = "VOLTAR",
                onClick = onBack,
                modifier = Modifier.weight(1f)
            )
            RachaiPrimaryButton(
                text = "SALVAR DESPESA",
                onClick = onSave,
                loading = state.saving,
                modifier = Modifier.weight(1.5f)
            )
        }
    }
}

@Composable
private fun SplitModeToggle(
    currentMode: SplitMode,
    onModeSelect: (SplitMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(RachaiRadius.field))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val equalSelected = currentMode == SplitMode.Equal
        val customSelected = currentMode == SplitMode.Custom

        // Botão Igualitária
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (equalSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                )
                .then(
                    if (equalSelected) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                    } else Modifier
                )
                .clickable { onModeSelect(SplitMode.Equal) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (equalSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Igualitária",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (equalSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (equalSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Botão Personalizada
        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(
                    if (customSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                )
                .then(
                    if (customSelected) {
                        Modifier.border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                    } else Modifier
                )
                .clickable { onModeSelect(SplitMode.Custom) },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (customSelected) {
                    Icon(
                        Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                }
                Text(
                    text = "Personalizada",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (customSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (customSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
