package org.example.rachai.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.example.rachai.theme.RachaiRadius
import org.example.rachai.theme.RachaiSpacing
import org.example.rachai.ui.components.RachaiAmountField
import org.example.rachai.ui.components.RachaiPrimaryButton
import org.example.rachai.ui.components.RachaiTextField

@Composable
fun ExpenseStep1(
    description: String,
    onDescriptionChange: (String) -> Unit,
    amountText: String,
    onAmountChange: (String) -> Unit,
    errorMessage: String?,
    onNext: () -> Unit,
    onScanReceipt: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = RachaiSpacing.md, vertical = RachaiSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(RachaiSpacing.md)
    ) {
        // Card de destaque para o Valor Total
        Card(
            shape = RoundedCornerShape(RachaiRadius.card),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    RoundedCornerShape(RachaiRadius.card)
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(RachaiSpacing.md)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Valor Total",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        Icons.Filled.Edit,
                        contentDescription = "Editar valor",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.height(RachaiSpacing.xs))

                RachaiAmountField(
                    value = amountText,
                    onValueChange = onAmountChange,
                    label = "Valor total (R$)",
                    supportingText = if (amountText.isBlank()) "Ex.: 150,00" else null,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Campo de Descrição
        RachaiTextField(
            value = description,
            onValueChange = onDescriptionChange,
            label = "Descrição",
            supportingText = if (description.isBlank()) "Ex.: Mercado, Jantar, Uber" else null
        )

        // Botão "Escanear nota fiscal"
        OutlinedButton(
            onClick = onScanReceipt,
            shape = RoundedCornerShape(RachaiRadius.field),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    Icons.Filled.Receipt,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "  Escanear nota fiscal",
                    style = MaterialTheme.typography.titleMedium
                )
            }
        }

        Text(
            text = "Uma forma mais rápida de preencher sua despesa.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        if (errorMessage != null) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = RachaiSpacing.xs)
            )
        }

        Spacer(modifier = Modifier.weight(1f, fill = false))

        // Botão AVANÇAR
        RachaiPrimaryButton(
            text = "AVANÇAR →",
            onClick = onNext,
            modifier = Modifier.padding(top = RachaiSpacing.md, bottom = RachaiSpacing.sm)
        )
    }
}
